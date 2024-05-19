package app.nik.messenger.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.nik.messenger.R
import app.nik.messenger.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import app.nik.messenger.data.Message
import app.nik.messenger.data.User
import app.nik.messenger.data.AuthState

import app.nik.messenger.domain.DataBaseHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = mBinding.root

        val textView: TextView = mBinding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        findNavController().navigate(R.id.nav_msg)

        mBinding.testButton.setOnClickListener{
            // Write a message to the database
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val userId = currentUser?.uid!!
            val db = DataBaseHandler()

            // Launch a coroutine to handle Firestore operations
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Create a User object with the current user ID
                    val user = User(userId, "Current User2")

                    // Set user name
//                    val isUserNameSet = db.setUserName(user)
//                    withContext(Dispatchers.Main) {
//                        if (!isUserNameSet) {
//                            mBinding.textHome.text = "Error setting user name."
//                            return@withContext
//                        }
//                    }

                    // Create a receiver User object (for testing purposes)
//                    val receiverUser = User("receiverUserId", "Receiver User")
//
//                    // Send a message from the current user to the receiver
//                    val messageContent = "Привет, как дела?"
//                    val timestamp = System.currentTimeMillis()
//                    val message = Message(messageContent, user.id, receiverUser.id, timestamp)
//                    val sendMessageResult = db.sendMessage(message)
//
//                    withContext(Dispatchers.Main) {
//                        if (sendMessageResult) {
//                            mBinding.textHome.text = "Message sent successfully."
//                        } else {
//                            mBinding.textHome.text = "Error sending message."
//                        }
//                    }
                    val receiverUser = User("receiverUserId", "Receiver User")
                    val msgs = db.getMessages(userId,receiverUser.id)
                    val messagesString = msgs.joinToString("\n") { message ->
                        "senderId: ${message.senderId}, receiverId: ${message.receiverId}, content: ${message.content}, timestamp: ${message.timestamp}"
                    }
                    Log.d("Home", messagesString)

                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        mBinding.textHome.text = "Error: ${e.message}"
                    }
                }
            }


        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}