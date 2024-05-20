package app.nik.messenger.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import app.nik.messenger.data.User
import app.nik.messenger.databinding.FragmentContactsBinding
import app.nik.messenger.ui.adapters.UserAdapter
import app.nik.messenger.ui.dialogs.AddUserDialog
import app.nik.messenger.ui.dialogs.AddUserDialogListener

class ContactsFragment : Fragment(), AddUserDialogListener {

    private var _binding: FragmentContactsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!
    private lateinit var mUserAdapter: UserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        initRecyclerView()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(ContactsViewModel::class.java)

        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val root: View = mBinding.root
        initRecyclerView()
        initButtons()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initRecyclerView() {
        mUserAdapter = UserAdapter(mutableListOf(), findNavController())
        mBinding.contactRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.contactRecyclerView.adapter = mUserAdapter

    }

    private fun observeViewModel(){

    }

    private fun initButtons() {
        mBinding.addBtn.setOnClickListener {
            val dialog = AddUserDialog()
            dialog.setAddDialogListener(this@ContactsFragment)
            dialog.show(parentFragmentManager, AddUserDialog.TAG)
        }
    }

    override fun onItemAdded(item: User) {
        mUserAdapter.pushBack(item)
    }
}