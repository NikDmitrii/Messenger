package app.nik.messenger.ui.auth
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit
import app.nik.messenger.data.AuthState


class AuthViewModel : ViewModel() {
    private val _numberText = MutableLiveData("")
    val numberText : LiveData<String> = _numberText

    private val _countryCode = MutableLiveData("")
    val countryCode : LiveData<String> = _countryCode

    private val _infoText = MutableLiveData("")
    val infoText : LiveData<String> = _infoText

    private val _verifyCode = MutableLiveData("")
    val verifyCode : LiveData<String> = _verifyCode

    private val _authState = MutableLiveData(AuthState.STATE_INITIALIZED)
    val authState : LiveData<AuthState> = _authState

    private var _mVerificationInProgress = false
    val mVerificationInProgress : Boolean
        get() = _mVerificationInProgress

    private var mStoredVerificationId: String? = null
    private val mFirebaseAuth: FirebaseAuth = Firebase.auth
    private var _mCredential : PhoneAuthCredential? = null
    val mCredential: PhoneAuthCredential?
        get() = _mCredential

    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // Вызывается, например, когда гугл сервисы автоматом видят код смс, без ввода
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            Log.d(TAG, "onVerificationCompleted:$credential")
            _mVerificationInProgress = false

            setAuthState(AuthState.STATE_VERIFY_SUCCESS)
            _mCredential = credential
            // Меняем состояние и шлем signInWithPhoneAuthCredential через fragment
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w(TAG, "onVerificationFailed", e)
            _mVerificationInProgress = false

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                setInfoText("Invalid phone number.")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                //Snackbar.make(view, "Quota exceeded.",
                    //Snackbar.LENGTH_SHORT).show()
            }

            // Show a message and update the UI
            setAuthState(AuthState.STATE_VERIFY_FAILED)
            //updateUI(AuthState.STATE_VERIFY_FAILED)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            mStoredVerificationId = verificationId
            mResendToken = token

            // Update UI
            setAuthState(AuthState.STATE_CODE_SENT)
            //updateUI(AuthState.STATE_CODE_SENT)
        }
    }

    fun startPhoneNumberVerification(activity : Activity) {
        if (!validatePhoneNumber()) {
            return
        }
        setInfoText("Wait code.")
        val options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
            .setPhoneNumber(countryCode.value!! + numberText.value!!)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        _mVerificationInProgress = true
    }

    fun verifyPhoneNumberWithCode(activity: Activity) {
        if(TextUtils.isEmpty(verifyCode.value))
        {
            setInfoText("code cannot be empty.")
            return
        }
        if(mStoredVerificationId == null)
        {
            setInfoText("verify id cannot be null.")
            return
        }
        _mCredential = PhoneAuthProvider.getCredential(mStoredVerificationId!!, verifyCode.value!!)
        signInWithPhoneAuthCredential(activity)
    }

    fun resendVerificationCode(activity: Activity) {
        if (!validatePhoneNumber()) {
            return
        }

        setInfoText("Wait resend code.")
        val optionsBuilder = PhoneAuthOptions.newBuilder(mFirebaseAuth)
            .setPhoneNumber(countryCode.value!! + numberText.value!!)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(mCallBack)
        if (mResendToken != null) {
            optionsBuilder.setForceResendingToken(mResendToken!!)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    fun signInWithPhoneAuthCredential(activity: Activity) {
        if(_mCredential == null)
        {
            return
        }
        mFirebaseAuth.signInWithCredential(_mCredential!!)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    setAuthState(AuthState.STATE_SIGNIN_SUCCESS)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    setAuthState(AuthState.STATE_SIGNIN_FAILED)
                }
                _mVerificationInProgress = false
            }
    }

    private fun validatePhoneNumber(): Boolean {
        if (TextUtils.isEmpty(numberText.value) && TextUtils.isEmpty(countryCode.value)) {
            setInfoText("Invalid phone number.")
            return false
        }

        return true
    }



    fun setNewNumber(value : String)
    {
        _numberText.value = value
    }

    fun setNewCountryCode(value : String)
    {
        _countryCode.value = value
    }

    fun setInfoText(value : String)
    {
        _infoText.value = value
    }

    fun setNewVerifyCode(value : String)
    {
        _verifyCode.value = value
    }

    fun setAuthState(value : AuthState)
    {
        _authState.value = value
    }

    companion object {
        private const val TAG = "PhoneAuth"
    }
}