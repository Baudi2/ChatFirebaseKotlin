package buildingThatApp.com

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import buildingThatApp.com.databinding.RegisterFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.util.*

class RegisterFragment : Fragment(R.layout.register_fragment) {

    private lateinit var binding: RegisterFragmentBinding
    private var selectedPhotoUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RegisterFragmentBinding.bind(view)

        binding.registerButtonRegister.setOnClickListener {
            performRegister(it)
        }

        binding.selectphotoButtonRegister.setOnClickListener {
            forPhotoButton()
        }

        binding.alreadyHaveAccountTextView.setOnClickListener {
            Log.d("testingLog", "Navigating to the Login fragment")
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment2()
            findNavController().navigate(action)
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

    // custom method, created this one just so that the onViewCreated wont be messy
    private fun performRegister(view: View) {
        val userName = binding.usernameEdittextRegister.text.toString()
        val email = binding.emailEdittextRegister.text.toString()
        val password = binding.passwordEdittextRegister.text.toString()

        Log.d("testingLog", "Username value is: $userName")
        Log.d("testingLog", "Email value is: $email")
        Log.d("testingLog", "Password value is: $password")

        // Getting access to the FireBase authenticator
        val myAuth = FirebaseAuth.getInstance()
        // creating new user with Email & password.
        /** We have to pass two arguments here, first one is user's email which we get through our usernameEditTextRegister and
         * second one is user's password which we get in similar fashion. Next, we have to setup completionListener, through "it"
         * we are going to get access to the actual result. If completion of the authentication was unsuccessful we'll return
         * @addOnCompleteListener. Also important to check that email & password aren't empty before adding then to fireBase.*/
        if (email.isNotEmpty() && password.isNotEmpty() && userName.isNotEmpty()) {
            myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // else if successful
                    hideKeyboard(view)
                    Log.d(
                        "testingLog",
                        "Successfully created user with uid: ${it.result?.user?.uid}"
                    )

                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    // Checking whether or not the creation of a user went successfully.
                    hideKeyboard(view)
                    Log.d("testingLog", "Failed to create user: ${it.message}")
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(activity, "Please fill in all the data", Toast.LENGTH_SHORT).show()
            hideKeyboard(view)
        }
    }

    private fun forPhotoButton() {
        Log.d("testingLog", "Try to show photo selector")
        // setting up intent to show photo selector. we ha to choose Action pick for photo selector
        val intent = Intent(Intent.ACTION_PICK)
        // setting the type of intent to tell it what we want to do
        intent.type = "image/*"
        // we need the result from picking photo from photo selector
        startActivityForResult(intent, 0)
    }

    // this method is called whenever we finish our intent from forPhotoButton()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // making sure that we are use correct requestCode, that retrieving result from intent went successfully and that the data we got is not null
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was
            Log.d("testingLog", "Photo was selected")

            /** Now we have to figure out what the photo actually is inside our app. through the [data] object we get we can access
             * to the uri of selected photo. Uri represent the location of where is the chosen photo is stored inside of the device.
             * And using that uri we can get access to the image as a bitmap. We have to use to different ways to get image though
             * bitmap because getBitmap is deprecated from 28 sdk version. So here on is of sdks less then 28 and one for those higher.*/
            selectedPhotoUri = data.data
            // I had to change this part since getBitmap method is deprecated
            try {
                selectedPhotoUri?.let {
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            activity?.contentResolver,
                            selectedPhotoUri
                        )
                        binding.selectphotoImageviewRegister.setImageBitmap(bitmap)
                        binding.selectphotoButtonRegister.alpha = 0f
                        //val bitmapDrawable = BitmapDrawable(this.resources, bitmap)
                        //binding.selectphotoButtonRegister.background = bitmapDrawable
                    } else {
                        val source = ImageDecoder.createSource(
                            requireActivity().contentResolver,
                            selectedPhotoUri!!
                        )
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        binding.selectphotoImageviewRegister.setImageBitmap(bitmap)
                        binding.selectphotoButtonRegister.alpha = 0f
                        //val bitmapDrawable = BitmapDrawable(this.resources, bitmap)
                        //binding.selectphotoButtonRegister.background = bitmapDrawable
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        // a little precaution
        if (selectedPhotoUri == null) return
        // first we are going to make a random string using UUID which is unique id. This gives us a really long unique string
        val filename = UUID.randomUUID().toString()
        // then, we need access to FirebaseStorage singleton object which is firebase storage upload area
        // We are going to save images inside image directory in firebase storage
        val firebaseStorage = FirebaseStorage.getInstance().getReference("/images/$filename")
        // now we'll try to send our image file to firebase storage
        firebaseStorage.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("testingLog", "Successfully uploaded image: ${it.metadata?.path}")

                //getting access to the file location
                firebaseStorage.downloadUrl.addOnSuccessListener {
                    Log.d("testingLog", "File location $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                //TODO: later do some logging here
            }
    }

    // we are going to safe and add to firebase db username and uid here
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        // path: is the string we are going to get to the users node along side with uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        // profileImageUrl is the file location of photo
        val user = User(uid, binding.usernameEdittextRegister.text.toString(),
            profileImageUrl, binding.passwordEdittextRegister.text.toString())

        // now are going to send our data (user object) firebase db
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("testingLog", "Finally we saved the user to Firebase Database")

                val action = RegisterFragmentDirections.actionRegisterFragmentToChatMainFragment2()
                findNavController().navigate(action)
            }
            .addOnFailureListener {
                Log.d("testingLog", "Failed to set value to database ${it.message}")
            }
    }
}







