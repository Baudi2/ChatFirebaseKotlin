package buildingThatApp.com.messages

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import buildingThatApp.com.R
import buildingThatApp.com.models.User
import buildingThatApp.com.models.UserItem
import buildingThatApp.com.databinding.NewMessageFragmentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class NewMessageFragment : Fragment(R.layout.new_message_fragment) {

    private lateinit var binding: NewMessageFragmentBinding

    companion object{
        private const val NEW_MESSAGE_LOG = "NewMessage"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NewMessageFragmentBinding.bind(view)

        fetchUsers()
    }

    // In this method well be fetching users from firebase db specifying the path to were all users are
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        // now we need to add listener - Add a listener for a single change in the data at this location. -
        //  - this listener will be triggered once with the value of the data at the location
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            // this method will get called every time we retrieve all of the users inside the firebase db
            override fun onDataChange(snapshot: DataSnapshot) {
                // creating an adapter for recyclerView via groupie external library
                // we are going to create a new adapter to fill it with user objects that we are going to fetch from firebase db
                val adapter = GroupAdapter<GroupieViewHolder>()

                // snapshot contains all of our data. we get access to each child (object of user) via foreach loop
                snapshot.children.forEach {
                    Log.d(NEW_MESSAGE_LOG, it.toString())
                    // converting the value that we receive into user object to work with them easier
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        // adding userItem object to our adapter and then attaching it to recyclerView
                        adapter.add(UserItem(user))
                    }
                }
                // handling item click so that onClick we'll open new fragment with chat
                adapter.setOnItemClickListener { item, _ ->
                    // item refers to the actual row that recyclerView is rendering, so called current row on which we press

                    // to get username out of item object and to be able to send it over to the chat screen we have to cast item as UserItem
                    val userItem = item as UserItem

                    // Then we just send the username over to the action since we can't navigate without passing username as an argument
                    // We put this username into the label field of ChatLogFragment via nav_graph and we define in its label field with
                    // pare of curly brackets that we want to put this argument into the label.
                    // passing here three arguments that we will need in the chat fragment
                    val action = NewMessageFragmentDirections.actionNewMessageFragmentToChatLogFragment(
                        userItem.user.username, userItem.user.profileImageUrl, userItem.user.uid)
                    /** In case if I'll later on need the ability to pass over to the ChatLogFragment whole user object, I'll have to go
                     * back to part 05 time 27:50 and what the explanation of parcelables.*/
                    findNavController().navigate(action)
                }

                binding.recyclerviewNewMessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}