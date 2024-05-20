package app.nik.messenger.ui.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import app.nik.messenger.R
import app.nik.messenger.data.Message
import app.nik.messenger.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.Serializable





interface ChangeUserDialogListener{
    fun onItemChanged(item : User)
}

interface UserChangeListener
{
    fun onItemChange(item : User)
}
class UserAdapter(val items : MutableList<User>,
                  val navController : NavController,
                  val listener : UserChangeListener? = null)
    : RecyclerView.Adapter<UserAdapter.ViewHolder>(), ChangeUserDialogListener
{
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val nameView : TextView = itemView.findViewById(R.id.user_name)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameView.text = items[position].name



        holder.nameView.setOnLongClickListener {
            callContextMenu(holder.itemView, holder.adapterPosition)
            true
        }

        holder.nameView.setOnClickListener{
            val user = Firebase.auth.currentUser
            val userId = user?.uid
            if(userId != null)
            {
                val senderId = userId
                val receiverId = items[position].id
                if(receiverId != null)
                {
                    val bundle = Bundle().apply {
                        putString("senderId", senderId)
                        putString("receiverId", receiverId)
                    }

                    navController.navigate(R.id.nav_msg, bundle)
                }

            }

        }


    }

    private fun callContextMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.context_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    erase(position)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    fun pushBack(item : User)
    {
        items.add(item)
        notifyItemInserted(items.size - 1)
        listener?.onItemChange(item)
    }

    fun erase(position : Int)
    {
        val eraseItem = items[position]
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
        listener?.onItemChange(eraseItem)
    }

    fun notifyItemChanged(item: User) {
        val position = items.indexOf(item)
        if (position != -1)
        {
            notifyItemChanged(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewUserList(list : MutableList<User>)
    {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onItemChanged(item: User)
    {
        notifyItemChanged(item)
    }
}