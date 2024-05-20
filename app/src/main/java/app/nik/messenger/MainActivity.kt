package app.nik.messenger

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import app.nik.messenger.databinding.ActivityMainBinding
import app.nik.messenger.domain.DataBaseHandler
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        setSupportActionBar(mBinding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = mBinding.drawerLayout
        val navView: NavigationView = mBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_auth || destination.id == R.id.nav_name) {
                // Блокируем drawer menu, когда открыт authFragment
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    actionBar?.setDisplayHomeAsUpEnabled(false)
            } else {
                // Разблокируем drawer menu для других фрагментов
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                actionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }

//        mAuth = Firebase.auth
//        if(mAuth.currentUser == null)
//        {
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(R.id.nav_auth, true)
//                .build()
//            navController.navigate(R.id.nav_auth,null, navOptions)
//        }
//        else
//        {
//            CoroutineScope(Dispatchers.Main).launch {
//                val db = DataBaseHandler()
//                val userId = Firebase.auth.currentUser!!.uid
//                if (db.userNameExistForId(userId)) {
//
//                    val navOptions = NavOptions.Builder()
//                        .setPopUpTo(R.id.nav_home, true)
//                        .build()
//                    //navController.navigate(R.id.nav_home,null, navOptions)
//                } else {
//                    val navOptions = NavOptions.Builder()
//                        .setPopUpTo(R.id.nav_name, true)
//                        .build()
//                    //navController.navigate(R.id.nav_name,null, navOptions)
//                }
//            }
//        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_contacts
            ), drawerLayout
        )


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}