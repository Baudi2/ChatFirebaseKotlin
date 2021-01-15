package buildingThatApp.com

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import buildingThatApp.com.databinding.NewMessageFragmentBinding

class NewMessageFragment: Fragment(R.layout.new_message_fragment) {

    private lateinit var binding : NewMessageFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = NewMessageFragmentBinding.bind(view)
        //TODO: haven't done anything here yet. Stopped at part 04 time 13:36

    }
}