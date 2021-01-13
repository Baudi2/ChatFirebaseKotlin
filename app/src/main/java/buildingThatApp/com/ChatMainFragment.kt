package buildingThatApp.com

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import buildingThatApp.com.databinding.ChatMainFragmentBinding

class ChatMainFragment : Fragment(R.layout.chat_main_fragment) {

    private lateinit var binding : ChatMainFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatMainFragmentBinding.bind(view)
    }
}