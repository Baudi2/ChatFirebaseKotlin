package buildingThatApp.com.registrationLogin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import buildingThatApp.com.R
import buildingThatApp.com.databinding.LoginFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

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
                        if (!it.isSuccessful) return@addOnCompleteListener

                        Log.d("testingLog", "Successfully logged in: ${it.result!!.user!!.uid}\"")
                        hideKeyboard(view)
                        val action = LoginFragmentDirections.actionLoginFragmentToChatMainFragment()
                        findNavController().navigate(action)
                    }
                    .addOnFailureListener {
                        Log.d("testingLog", "Can't login because: ${it.message}")
                        Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
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

    // To hide the keyboard after the button is pressed
    private fun hideKeyboard(view: View) {
        try {
            val imn =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imn.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
        }
    }
}