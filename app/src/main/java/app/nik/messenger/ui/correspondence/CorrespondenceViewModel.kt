package app.nik.messenger.ui.correspondence

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.nik.messenger.data.Message
import app.nik.messenger.domain.DataBaseHandler

class CorrespondenceViewModel : ViewModel() {
    private val _msgText = MutableLiveData("")
    val msgText : LiveData<String> = _msgText
    private val mDb = DataBaseHandler()
    suspend fun sendMsg(sender : String, receiver : String)
    {
        val timestamp = System.currentTimeMillis()
        val message = Message(_msgText.value ?: "",sender,receiver,timestamp )
        mDb.sendMessage(message)
    }
    fun setMsgText(value : String) {
        _msgText.value = value
    }
}