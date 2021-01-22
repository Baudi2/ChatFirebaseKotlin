package buildingThatApp.com.messages

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import buildingThatApp.com.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatToItem(val text: String) : Item<GroupieViewHolder>() {
    // look up explanation in ChatFromItem
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textview_chat_right_to).text = text
    }

    // This layout is going to be a row that renders out chat messages. here we are returning layout file  -
    // - for left_from perspective
    override fun getLayout() =
        R.layout.chat_right_to
}