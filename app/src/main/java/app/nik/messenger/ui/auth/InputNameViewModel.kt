package app.nik.messenger.ui.auth

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import app.nik.messenger.R
import app.nik.messenger.data.User
import app.nik.messenger.domain.DataBaseHandler
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.fragment.findNavController
import app.nik.messenger.MainActivity
import com.google.android.material.navigation.NavigationView

class InputNameViewModel : ViewModel() {
    private val mDb = DataBaseHandler()
    private val _nameText = MutableLiveData("")
    val nameText : LiveData<String> = _nameText

    private val _infoText = MutableLiveData("")
    val infoText : LiveData<String> = _infoText

    fun setNameInBase(navController: NavController, activity: Activity?)
    {
        val userId = Firebase.auth.currentUser!!.uid
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Create a User object with the current user ID
                val user = User(userId, _nameText.value ?: "")

                    val isUserNameSet = mDb.setUserName(user)
                    withContext(Dispatchers.Main) {
                        if (!isUserNameSet) {
                            setInfo("Error, set user name.")
                            return@withContext
                        }
                        else
                        {
                            setInfo("Name set.")
                            (activity as? MainActivity)?.let { mainActivity ->
                                val navigationView: NavigationView = mainActivity.findViewById(R.id.nav_view)
                                val headerView: View = navigationView.getHeaderView(0)
                                val drawerTextView: TextView = headerView.findViewById(R.id.drawer_title)
                                drawerTextView.text = _nameText.value
                            }
                            navController.navigate(R.id.nav_home)
                        }
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    setInfo("Error: ${e.message}")
                }
            }
        }
    }

    fun setName(value : String)
    {
        _nameText.value = value
    }

    fun setInfo(value : String)
    {
        _infoText.value = value
    }
}