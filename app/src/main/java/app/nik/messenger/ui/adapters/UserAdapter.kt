package app.nik.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import app.nik.messenger.R
import app.nik.messenger.data.Message
import app.nik.messenger.data.User
import java.io.Serializable




interface userChangeListener
{
    fun onItemChange(item : User)
}

interface ChangeUserDialogListener{
    fun onItemChanged(item : User)
}
class UserAdapter(val items : MutableList<User>,
                     val listener : userChangeListener? = null,
                     val hasContextMenu: Boolean = true)
    : RecyclerView.Adapter<UserAdapter.ViewHolder>(), ChangeUserDialogListener
{
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val nameView : TextView = itemView.findViewById(R.id.user_name)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameView.text = items[position].name



        if(hasContextMenu)
        {
            holder.nameView.setOnLongClickListener {
                callContextMenu(holder.itemView, holder.adapterPosition)
                true
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

    override fun onItemChanged(item: User)
    {
        notifyItemChanged(item)
    }
}