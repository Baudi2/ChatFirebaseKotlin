package buildingThatApp.com

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import buildingThatApp.com.databinding.ChatMainFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class ChatMainFragment : Fragment(R.layout.chat_main_fragment) {

    private lateinit var binding : ChatMainFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatMainFragmentBinding.bind(view)
        verifyUserIsLoggedIn()
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val action = ChatMainFragmentDirections.actionGlobalRegisterFragment()
            findNavController().navigate(action)
        }
    }
}