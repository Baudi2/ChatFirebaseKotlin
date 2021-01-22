package buildingThatApp.com.messages

import android.widget.TextView
import buildingThatApp.com.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatFromItem(val text: String) : Item<GroupieViewHolder>() {
    // To change the text inside the bubbles (message windows) we'll be using this method. Same in ChatToItem
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // getting access to the textView which display the message text. Same can be done in ChatToItem
        viewHolder.itemView.findViewById<TextView>(R.id.textview_chat_left_from).text = text
    }

    // This layout is going to be a row that renders out chat messages. here we are returning layout file  -
    // - for left_from perspective
    override fun getLayout() =
        R.layout.chat_left_from
}