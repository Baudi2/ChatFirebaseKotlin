package buildingThatApp.com

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import buildingThatApp.com.databinding.LoginFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.login_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = LoginFragmentBinding.bind(view)

        binding.loginButtonLogin.setOnClickListener {
            val email = binding.emailEdittextLogin.text.toString()
            val password = binding.passwordEdittextLogin.text.toString()

            // Getting an instance of fireBase to perform login transaction
            if (email.isNotEmpty() && password.isNotEmpty()) {
                val singIn = FirebaseAuth.getInstance()
                singIn.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                    }
                    .addOnFailureListener {

                    }
            } else {
                Toast.makeText(activity, "Please enter all the data", Toast.LENGTH_SHORT).show()
            }
            Log.d("testingLog", "Attempt login with email: $email and password: $password")
        }

        binding.backToRegisterTextView.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}