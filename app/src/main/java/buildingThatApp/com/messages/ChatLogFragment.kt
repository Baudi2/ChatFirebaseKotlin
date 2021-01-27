package buildingThatApp.com.messages

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import buildingThatApp.com.R
import buildingThatApp.com.databinding.ChatLogFragmentBinding
import buildingThatApp.com.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ChatLogFragment : Fragment(R.layout.chat_log_fragment) {

    private lateinit var binding: ChatLogFragmentBinding
    private val args: ChatLogFragmentArgs by navArgs()
    private val adapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        private const val CHAT_FRAGMENT_LOGS = "chatMyLogs"
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatLogFragmentBinding.bind(view)

        //setupDummyData() - we no longer need this method since we are now going to fill recyclerView with real data

        binding.recyclerviewChatLog.adapter = adapter
        listenForMessages()

        // here we'll be setting up send message button
        binding.sendButtonChatLog.setOnClickListener {
            sendMessage()
        }

        // these of code insure that when we write new message recyclerView will automatically scroll down to include it -
        // - also when we open a chat we recyclerView already will be at the lowest position
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (itemCount != -1 || itemCount != 0) {
                    binding.recyclerviewChatLog.scrollToPosition(positionStart - itemCount +1)
                }
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                binding.recyclerviewChatLog.scrollToPosition(itemCount)
            }
        })

        // these lines of code insure that when we open keyboard the keyboard wont go over the messages and instead -
        // - push them up
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.recyclerviewChatLog.layoutManager = layoutManager

        binding.recyclerviewChatLog.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.recyclerviewChatLog.postDelayed({
                    if (binding.recyclerviewChatLog.adapter!!.itemCount != -1) {
                        binding.recyclerviewChatLog.scrollToPosition(
                            binding.recyclerviewChatLog.adapter!!.itemCount - 1
                        )
                    }
                }, 100)
            }
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = args.toUid
        // here we need access to Firebase database's messages node
        // we will modify node from which we listen because now we have dm structure of messages. explained in sendMessage()
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        // and now we want to add a listener to this reference
        // this listener will notify us about every piece of data that belongs to messages node
        ref.addChildEventListener(object : ChildEventListener {
            // in here we have to implement all the methods from ChildEventListener interface, we wont using all of them.


            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                /**we can get the value using our ChatMessage class. the value is stored inside snapshot object
                we use ChatMessage to get value because we saved everything as this class
                we should also make null check before dealing with data we receive*/
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                Log.d(CHAT_FRAGMENT_LOGS, chatMessage.text)

                // we will be using different viewHolder for adapter depending of whether or uid is of current user or of the other
                if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                    adapter.add(ChatToItem(chatMessage.text, args.currentUserPhoto))
                } else {
                    // adding object to our adapter to display them in chat fragment.
                    // we send text of the message and photo of a user (user photo is the to whom we are writing)
                    adapter.add(ChatFromItem(chatMessage.text, args.userPhoto))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        Log.d(CHAT_FRAGMENT_LOGS, "Attempt to send message.....")

        // getting text from the editText, and then erasing it once it has been successfully delivered
        val text = binding.edittextChatLog.text.toString()

        // getting FromId through firebase uid
        val fromId = FirebaseAuth.getInstance().uid
        // we'll get toUi through argument we pass here when we open this chat through new message fragment
        val toId = args.toUid
        // making null check
        if (fromId == null) return

        /** Setting up Firebase messaging. path: - is the name of the node in firebase where our "message" object will be stored.
         * .push() - will generate an automatic node to start saving data in. We are going to change the structure by which we push messages
         *  to insure that all users will have their own lists of messages and that all messages out there wont be mashed into one chat.
         *  in new path we create new node called user-messages and inside that node we create unique node that contains uid of current user
         *  and user to whom we are writing. This way we insure that only these aforementioned users can access those messages.*/
        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push() - old version
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        // this reference if for user to whom we are writing so that the message we write to him will show up on his screen as well
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        // including third reference for latest messages. since we are not using .push() call here when we send new message -
        // - firebase wont create different node for it and instead will override the text from existing one hence the name latest message
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        // same as toReference
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

            if (text.isNotEmpty()) {
            // now here we have to pass quite a few parameters, those are id, text, fromId, toId, timeStamp. (important, this is our custom class)
            // we can get timestamp through System.currentTimeMillis() / 1000, the division is needed to turn milliseconds into regular seconds
            val chatMessage =
                ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

            // we will send here chatMessage objects
            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    binding.edittextChatLog.text.clear()
                    // logging successful attempt and id. id new id generated by push call
                    Log.d(CHAT_FRAGMENT_LOGS, "Saved our chat message.....${reference.key}")
                }

            toReference.setValue(chatMessage)

            latestMessageRef.setValue(chatMessage)

            latestMessageToRef.setValue(chatMessage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val view = activity?.currentFocus
        if (view != null) {
            val imn = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imn.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}




