package app.nik.messenger.ui.auth

import android.util.Log
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
class InputNameViewModel : ViewModel() {
    private val mDb = DataBaseHandler()
    private val _nameText = MutableLiveData("")
    val nameText : LiveData<String> = _nameText

    private val _infoText = MutableLiveData("")
    val infoText : LiveData<String> = _infoText

    fun setNameInBase(navController: NavController)
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