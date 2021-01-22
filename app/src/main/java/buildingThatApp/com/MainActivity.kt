package buildingThatApp.com

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {
    // класс который знает как разбирается с навигацией
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // инициализировали наш navController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        // Тут мы определяем высокоуровневые экраны на которых back button не будет виден!
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.chatMainFragment, R.id.registerFragment, R.id.loginFragment)
        )

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() ||super.onSupportNavigateUp()
    }


}
/*
below you can see the code that stupid people write

This is not needed since we have setup our actionBar with navController and it can get the label directly from navGraph

            title = when(destination.id) {
                R.id.registerFragment -> "Registration"
                R.id.loginFragment -> "Login"
                R.id.chatMainFragment -> "Messenger"
                R.id.newMessageFragment -> "Select User"
                else -> "KotlinMessengerFirebase"
            }

        // меняет заголовки в actionBar в зависимости от того какой фрагмент сейчас на экране
        navController.addOnDestinationChangedListener { controller, destination, argument ->
            // compare destination id

            if (destination.id != controller.graph.startDestination) {
                actionBarTrue()
            }
            if (destination.id == controller.graph.startDestination) {
                actionBarFalse()
            }
            if (destination.id == R.id.registerFragment) {
                actionBarFalse()
            }
            if (destination.id == R.id.loginFragment) {
                actionBarFalse()
            }
        }
    }

    private fun actionBarFalse() {
        val theBar = supportActionBar
        theBar?.setDisplayHomeAsUpEnabled(false)
        theBar?.setHomeButtonEnabled(false)
    }

    private fun actionBarTrue() {
        val theBar = supportActionBar
        theBar?.setDisplayHomeAsUpEnabled(true)
        theBar?.setHomeButtonEnabled(true)
    }
 */