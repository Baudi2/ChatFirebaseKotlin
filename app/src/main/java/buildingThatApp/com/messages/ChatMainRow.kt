package buildingThatApp.com.messages

import android.widget.TextView
import buildingThatApp.com.R
import buildingThatApp.com.models.ChatMessage
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

// viewHolder for home screen adapter
class ChatMainRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.message_textView_chat_main_row).text = chatMessage.text
    }

    override fun getLayout() =
        R.layout.chat_main_row
}