package buildingThatApp.com

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

class MainActivity : AppCompatActivity() {
    // класс который знает как разбирается с навигацией
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // инициализировали наш navController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        // меняет заголовки в actionBar в зависимости от того какой фрагмент сейчас на экране
        navController.addOnDestinationChangedListener { controller, destination, argument ->
            // compare destination id
            title = when(destination.id) {
                R.id.registerFragment -> "Registration"
                R.id.loginFragment -> "Login"
                R.id.chatMainFragment -> "Messenger"
                else -> "KotlinMessengerFirebase"
            }
        }
    }
}