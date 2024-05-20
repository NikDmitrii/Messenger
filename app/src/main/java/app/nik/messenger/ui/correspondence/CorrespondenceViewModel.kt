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
        setMsgText("")
        mDb.sendMessage(message)
    }
    fun setMsgText(value : String) {
        _msgText.value = value
    }

    fun setMsgListener(sender : String, receiver : String, onUpdate: (List<Message>) -> Unit){
        mDb.listenForNewMessages(sender,receiver, onUpdate)
    }

    suspend fun getMessages(senderId : String , receiverId : String) : List<Message>
    {
        return mDb.getMessages(senderId, receiverId)
    }

    override fun onCleared() {
        mDb.deleteMessageListener()
        super.onCleared()
    }
}