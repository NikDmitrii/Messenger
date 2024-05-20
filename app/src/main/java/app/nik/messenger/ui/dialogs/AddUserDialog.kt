package app.nik.messenger.ui.dialogs

import app.nik.messenger.R
import app.nik.messenger.data.User


import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

interface AddUserDialogListener {
    fun onItemAdded(item: User)
}



class AddUserDialog() : DialogFragment()
{
    private var listener: AddUserDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
    }

    fun setAddDialogListener(listener: AddUserDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.add_user_dialog, container, false)

        initButton(view)


        return view
    }

    private fun initButton(view : View)
    {
        val addBtn : Button = view.findViewById(R.id.add_button)
        addBtn.setOnClickListener{
            val name : String = view.findViewById<EditText>(R.id.add_user_edit_text).text.toString()

            dismiss()
        }

        val cancelBtn : Button = view.findViewById(R.id.cancel_button)
        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    companion object{
        const val TAG = "ADD_USER_DIALOG"
    }
}