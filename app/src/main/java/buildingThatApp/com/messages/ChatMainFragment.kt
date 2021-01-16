package buildingThatApp.com.messages

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import buildingThatApp.com.R
import buildingThatApp.com.databinding.ChatMainFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class ChatMainFragment : Fragment(R.layout.chat_main_fragment) {

    private lateinit var binding: ChatMainFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatMainFragmentBinding.bind(view)
        // includes options menu for this fragment
        setHasOptionsMenu(true)
        verifyUserIsLoggedIn()
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val action = ChatMainFragmentDirections.actionGlobalRegisterFragment()
            findNavController().navigate(action)
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
                val action = ChatMainFragmentDirections.actionChatMainFragmentToNewMessageFragment()
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
}