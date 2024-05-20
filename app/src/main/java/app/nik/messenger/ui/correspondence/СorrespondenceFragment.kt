package app.nik.messenger.ui.correspondence

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.nik.messenger.data.Message
import app.nik.messenger.databinding.FragmentCorrespondenceBinding
import app.nik.messenger.ui.auth.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class СorrespondenceFragment : Fragment() {
    private var _binding: FragmentCorrespondenceBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mViewModel: CorrespondenceViewModel
    private var mReceiver : String? = null
    private var mSender : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCorrespondenceBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(requireActivity()).get(CorrespondenceViewModel::class.java)
        val root: View = mBinding.root
        observeViewModel()
        bindButtons()

        mReceiver = arguments?.getString("receiverId")
        mSender = arguments?.getString("senderId")

        if(mSender == null || mReceiver == null)
        {
            findNavController().popBackStack()
        }

        return root
    }

    private fun bindButtons(){
        mBinding.messageSendBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    mViewModel.sendMsg(Firebase.auth.currentUser!!.uid, mReceiver!!)
                } catch (e: Exception) {
                    // Обработка ошибок
                    e.printStackTrace()
                }
            }
        }
    }

    private fun observeViewModel(){


        mViewModel.msgText.observe(viewLifecycleOwner) {
            if(!TextUtils.equals(mBinding.chatMessageInput.text,it.toString()))
            {
                mBinding.chatMessageInput.setText(it.toString())
            }
        }
        mBinding.chatMessageInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val newStr = s.toString()
                if(!TextUtils.equals(newStr,mViewModel.msgText.value))
                {
                    mViewModel.setMsgText(newStr)
                }
            }
        })
    }
}