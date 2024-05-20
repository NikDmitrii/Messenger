package app.nik.messenger.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import app.nik.messenger.R
import app.nik.messenger.databinding.FragmentSettingsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SettingsFragment : PreferenceFragmentCompat() {

    //private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    //private val mBinding get() = _binding!!
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_pref, rootKey)

        // Находим кнопку выхода по ключу
        val logoutButton = findPreference<Preference>("logout_button")
        // Устанавливаем обработчик нажатия на кнопку
        logoutButton?.setOnPreferenceClickListener {
            Firebase.auth.signOut()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_auth, true)
                .build()

            findNavController().navigate(R.id.action_nav_settings_to_nav_auth, null, navOptions)
            true
        }
    }
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val galleryViewModel =
//            ViewModelProvider(this).get(SettingsViewModel::class.java)
//
//        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        val root: View = mBinding.root
//
//        val textView: TextView = mBinding.textSettings
//        galleryViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}