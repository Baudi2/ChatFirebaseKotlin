package buildingThatApp.com.messages

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
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
        // here we need access to Firebase database's messages node
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

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


        // setting up Firebase messaging. path: - is the name of the node in firebase where our "message" object will be stored.
        // .push() - will generate an automatic node to start saving data in
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        // getting FromId through firebase uid
        val fromId = FirebaseAuth.getInstance().uid
        // we'll get toUi through argument we pass here when we open this chat through new message fragment
        val toId = args.toUid
        // making null check
        if (fromId == null) return

        if(text.isNotEmpty()) {
            // now here we have to pass quite a few parameters, those are id, text, fromId, toId, timeStamp. (important, this is our custom class)
            // we can get timestamp through System.currentTimeMillis() / 1000, the division is needed to turn milliseconds into regular seconds
            val chatMessage =
                ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

            // we will send here chatMessage objects
            reference.setValue(chatMessage)
                .addOnSuccessListener {
                    binding.edittextChatLog.setText("")
                    // logging successful attempt and id. id new id generated by push call
                    Log.d(CHAT_FRAGMENT_LOGS, "Saved our chat message.....${reference.key}")
                }
        } else {
            Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
        }
    }
}


/*
// these lines of code are responsible for recyclerView correct positioning when we open keyboard
        // (completely optional part: might as well delete it)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.recyclerviewChatLog.layoutManager = layoutManager

        binding.recyclerviewChatLog.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.recyclerviewChatLog.postDelayed({
                    binding.recyclerviewChatLog.scrollToPosition(
                        binding.recyclerviewChatLog.adapter!!.itemCount - 1
                    )
                }, 100)
            }
        }

        I left this code here for now since when there is no messages and you try to open keyboard the app crushes.
        I guess it is because there is no big difference between oldLayout and new one
 */


