package app.nik.messenger.ui.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import app.nik.messenger.R
import app.nik.messenger.data.Message
import app.nik.messenger.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.Serializable




interface itemChangeListener
{
    fun onItemChange(item : Message)
}

interface ChangeDialogListener{
    fun onItemChanged(item : Message)
}
class MessageAdapter(val items : MutableList<Message>,
                          val listener : itemChangeListener? = null)
    : RecyclerView.Adapter<MessageAdapter.ViewHolder>(), ChangeDialogListener
{
    private val mUserId : String
    init{
        mUserId = Firebase.auth.currentUser?.uid ?: ""
    }
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val messageView : TextView = itemView.findViewById(R.id.message_text)
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
        holder.messageView.text = items[position].content



        holder.messageView.setOnLongClickListener {
            callContextMenu(holder.itemView, holder.adapterPosition)
            true
        }

        // Чужое сообщение
        if(!TextUtils.equals(items[position].senderId, mUserId)) {
            holder.messageView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_500))
        } // Свое сообщение
        else {
            holder.messageView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_200))
        }
        holder.messageView.textSize = 18f

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

    fun pushBack(item : Message)
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

    fun notifyItemChanged(item: Message) {
        val position = items.indexOf(item)
        if (position != -1)
        {
            notifyItemChanged(position)
        }
    }

    override fun onItemChanged(item: Message)
    {
        notifyItemChanged(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewMessageList(list : List<Message>)
    {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}