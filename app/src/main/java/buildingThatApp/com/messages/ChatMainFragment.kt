package buildingThatApp.com.messages

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import buildingThatApp.com.R
import buildingThatApp.com.databinding.ChatMainFragmentBinding
import buildingThatApp.com.models.ChatMessage
import buildingThatApp.com.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ChatMainFragment : Fragment(R.layout.chat_main_fragment) {

    private lateinit var binding: ChatMainFragmentBinding
    private lateinit var currentUser : User
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatMainFragmentBinding.bind(view)
        // includes options menu for this fragment
        setHasOptionsMenu(true)
        // here we make sure whether or not user have already logged in to our app or not, if not we sent him to the registration screen.
        verifyUserIsLoggedIn()
        //attaching adapter to the recyclerView
        binding.recyclerViewChatMainFragment.adapter = adapter
        // here we will be listening for latest messages
        listenForLatestMessages()

        //TODO: setup text view in [chat_main_fragment] layout that will display text saying "you have no chats yet" -
    // - TODO: if the user have chatted anyone yet, and hide if that statement is no longer valid.
    }


    // if user is not logged in we don't want to fetch his profile pic that's why we placed called to the fetchCurrentUser() -
    // - method inside else statement, so that when he finally logs in when only then fetch his data (profile pic)
    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val action = ChatMainFragmentDirections.actionGlobalRegisterFragment()
            findNavController().navigate(action)
        } else {
            // here we will fetch current user's photo and send as an argument to the chat screen
            fetchCurrentUser()
        }
    }

    // Creating toolbar with new chat & logout options
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // we need to pass first our menu resource and then the menu object we get pass into this method.
        requireActivity().menuInflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // handles clicks on menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_message -> {
                // opens fragment where you can choose a new chat to start
                val action = ChatMainFragmentDirections.actionChatMainFragmentToNewMessageFragment(currentUser.profileImageUrl, currentUser.uid)
                findNavController().navigate(action)
            }
            R.id.menu_sign_out -> {
                // signing out of the account & navigation to registration screen
                // calling Firebase's inbuilt signOut method to sign out of the account we are currently in
                FirebaseAuth.getInstance().signOut()
                val action = ChatMainFragmentDirections.actionGlobalRegisterFragment()
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("testingLog", "this is the uid: $uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)!!
                Log.d("testingLog", "this is the photo url: ${currentUser.profileImageUrl}")
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest/messages/$fromId")
        // listening for the new nodes that are appearing underneath this reference
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // we are going to be notified every time we see new child for the latest messages
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                adapter.add(ChatMainRow())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}







