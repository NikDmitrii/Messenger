package app.nik.messenger.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
import app.nik.messenger.ui.adapters.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val mBinding get() = _binding!!
    private lateinit var mViewModel : HomeViewModel
    private lateinit var mUserAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = mBinding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        initRecyclerView()

    }

    private fun observeViewModel()
    {
        // При наблюдении за RecyclerView были ошибки, было сложно его проектировать,
        // поэтому пока он не наблюдается вью моделью
    }

    private fun initRecyclerView()
    {
        mUserAdapter = UserAdapter(mutableListOf(), findNavController())
        mBinding.contactRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mBinding.contactRecyclerView.adapter = mUserAdapter

        mUserAdapter.pushBack(User("Testid","Tim"))
        mUserAdapter.pushBack(User("Testid2","Tom"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}