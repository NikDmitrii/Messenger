package app.nik.messenger.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.nik.messenger.databinding.FragmentInputNameBinding
class InputNameFragment : Fragment(){
    private var _binding: FragmentInputNameBinding? = null
    private val mBinding get() = _binding!!
    private var mOnBackPressedCallback: OnBackPressedCallback? = null
    private lateinit var mViewModel : InputNameViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInputNameBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(requireActivity()).get(InputNameViewModel::class.java)
        val root: View = mBinding.root
        observeViewModel()
        bindButtons()

        return root

    }

    fun observeViewModel(){
        mViewModel.nameText.observe(viewLifecycleOwner) {
            if(!TextUtils.equals(mBinding.nameText.text,it.toString()))
            {
                mBinding.nameText.setText(it.toString())
            }
        }
        mBinding.nameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val newStr = s.toString()
                if(!TextUtils.equals(newStr,mViewModel.nameText.value))
                {
                    mViewModel.setName(newStr)
                }

            }
        })

        mViewModel.infoText.observe(viewLifecycleOwner) {
            if(!TextUtils.equals(mBinding.nameText.text,it.toString()))
            {
                mBinding.nameInfo.setText(it.toString())
            }
        }
    }

    fun bindButtons(){
        mBinding.sendName.setOnClickListener{
            mViewModel.setNameInBase(findNavController())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mOnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Ничего не делаем, если нажата кнопка назад
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, mOnBackPressedCallback!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mOnBackPressedCallback?.remove()
        _binding = null
    }
}