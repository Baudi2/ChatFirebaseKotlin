package buildingThatApp.com.messages

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
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
        private val NEW_MESSAGE_LOG = "NewMessage"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NewMessageFragmentBinding.bind(view)

        // creating an adapter for recyclerView via groupie external library
        //val adapter = GroupAdapter<GroupieViewHolder>()

        // we need to add objects to display in our adapter. UserItem is a class we made with 2 methods in it
        // The difference between regular recyclerView here is that adapter.add(UserItem()) will show only one row,
        // if we want to add more we'll have to call adapter.add() again
        //adapter.add(UserItem())
        //adapter.add(UserItem())
        //adapter.add(UserItem())

        // attaching generated adapter to the recyclerView
        //binding.recyclerviewNewMessage.adapter = adapter

        fetchUsers()
    }

    // In this method well be fetching users from firebase db specifying the path to were all users are
    private fun fetchUsers() {
        var ref = FirebaseDatabase.getInstance().getReference("/users")
        // now we need to add listener - Add a listener for a single change in the data at this location. -
        //  - this listener will be triggered once with the value of the data at the location
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            // this method will get called every time we retrieve all of the users inside the firebase db
            override fun onDataChange(snapshot: DataSnapshot) {
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
                binding.recyclerviewNewMessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}