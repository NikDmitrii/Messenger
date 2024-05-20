package app.nik.messenger.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.nik.messenger.R
import app.nik.messenger.databinding.FragmentAuthBinding
import app.nik.messenger.ui.home.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import app.nik.messenger.data.AuthState
import app.nik.messenger.domain.DataBaseHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mAuthViewModel: AuthViewModel
    private var mOnBackPressedCallback: OnBackPressedCallback? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        mAuthViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        val root: View = mBinding.root
        observeViewModel()
        bindButtons()

        return root
    }

    private fun observeViewModel() {
        // Синхронизация номера телефона
        mAuthViewModel.numberText.observe(viewLifecycleOwner) {
            if(!TextUtils.equals(mBinding.numberText.text,it.toString()))
            {
                mBinding.numberText.setText(it.toString())
            }
        }
        mBinding.numberText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val newStr = s.toString()
                if(!TextUtils.equals(newStr,mAuthViewModel.numberText.value))
                {
                    mAuthViewModel.setNewNumber(newStr)
                }

            }
        })

        // Синхронизация выбора кода телефона
        if(TextUtils.equals(mAuthViewModel.countryCode.value, ""))
        {
            val countryCode = mBinding.codePicker.selectedCountryCodeWithPlus
            mAuthViewModel.setNewCountryCode(countryCode)
        }

        mBinding.codePicker.setOnCountryChangeListener {
            val countryCode = mBinding.codePicker.selectedCountryCodeWithPlus
            if(!TextUtils.equals(countryCode, mAuthViewModel.countryCode.value))
            {
                mAuthViewModel.setNewCountryCode(countryCode)
            }
        }

        mAuthViewModel.countryCode.observe(viewLifecycleOwner) { countryCode ->
            if(countryCode != null && countryCode != "" &&
                !TextUtils.equals(countryCode, mBinding.codePicker.selectedCountryCodeWithPlus))
            {
                mBinding.codePicker.setCountryForPhoneCode(countryCode.toInt())
            }
        }

        mAuthViewModel.infoText.observe(viewLifecycleOwner){
            mBinding.infoText.text = it
        }

        mBinding.verifyCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val newStr = s.toString()
                if(!TextUtils.equals(newStr,mAuthViewModel.verifyCode.value))
                {
                    mAuthViewModel.setNewVerifyCode(newStr)
                }
            }
        })

        mAuthViewModel.verifyCode.observe(viewLifecycleOwner) {
            if(!TextUtils.equals(mBinding.verifyCode.text,it.toString()))
            {
                mBinding.verifyCode.setText(it.toString())
            }
        }

        mAuthViewModel.authState.observe(viewLifecycleOwner){ state->
            updateUI(state)
        }
    }

    private fun bindButtons() {
        mBinding.sendBtn.setOnClickListener{
            mAuthViewModel.startPhoneNumberVerification(requireActivity())
        }

        mBinding.resendBtn.setOnClickListener {
            mAuthViewModel.resendVerificationCode(requireActivity())
        }

        mBinding.verifyBtn.setOnClickListener {
            mAuthViewModel.verifyPhoneNumberWithCode(requireActivity())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mOnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Ничего не делаем, если нажата кнопка назад
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mOnBackPressedCallback!!)
    }

    private fun updateUI(uiState: AuthState) {
        when (uiState) {
            AuthState.STATE_INITIALIZED -> {
                // Initialized state, show only the phone number field and start button
                enableViews(mBinding.numberText, mBinding.codePicker, mBinding.sendBtn)
                disableViews(mBinding.verifyCode, mBinding.resendBtn, mBinding.verifyBtn)
                mAuthViewModel.setInfoText("")
            }
            AuthState.STATE_CODE_SENT -> {
                // Code sent state, show the verification field, the
                enableViews(mBinding.numberText, mBinding.resendBtn,
                    mBinding.codePicker, mBinding.sendBtn,mBinding.verifyCode,mBinding.verifyBtn)

                disableViews(mBinding.sendBtn)
                mAuthViewModel.setInfoText("Code send.")

            }
            AuthState.STATE_VERIFY_FAILED -> {
                // Verification has failed, show all options
//                enableViews(binding.buttonStartVerification, binding.buttonVerifyPhone,
//                    binding.buttonResend, binding.fieldPhoneNumber,
//                    binding.fieldVerificationCode)
//                binding.detail.setText(R.string.status_verification_failed)
                mAuthViewModel.setInfoText("Verify failed.")
            }
            AuthState.STATE_VERIFY_SUCCESS -> {
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mBinding.verifyCode, mBinding.resendBtn,
                    mBinding.verifyBtn, mBinding.resendBtn,mBinding.numberText)
                mAuthViewModel.setInfoText("Verify success.")

                // Set the verification text based on the credential
                val credential = mAuthViewModel.mCredential
                if (credential != null) {
                    if (credential.smsCode != null) {
                        mAuthViewModel.setNewVerifyCode(credential.smsCode!!)
                    } else {
                        mAuthViewModel.setNewVerifyCode("(instant validation)")
                    }
                    mAuthViewModel.signInWithPhoneAuthCredential(requireActivity())
                }
            }
            AuthState.STATE_SIGNIN_FAILED ->
                mAuthViewModel.setInfoText("Invalid code.")
            AuthState.STATE_SIGNIN_SUCCESS -> {
                mAuthViewModel.setInfoText("Sign in success.")
                CoroutineScope(Dispatchers.Main).launch {
                    val db = DataBaseHandler()
                    val userId = Firebase.auth.currentUser!!.uid
                    if (db.userNameExistForId(userId)) {
                        findNavController().navigate(R.id.action_nav_auth_to_nav_home)
                    } else {
                        findNavController().navigate(R.id.action_nav_auth_to_inputNameFragment)
                    }
                }
            }
        }
    }
    private fun enableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = true
        }
    }

    private fun disableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            updateUI(AuthState.STATE_SIGNIN_SUCCESS)
        } else {
            updateUI(AuthState.STATE_INITIALIZED)
        }

        if (mAuthViewModel.mVerificationInProgress) {
            mAuthViewModel.startPhoneNumberVerification(requireActivity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mOnBackPressedCallback?.remove()
        _binding = null
    }
}