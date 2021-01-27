package buildingThatApp.com.messages

import buildingThatApp.com.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

// viewHolder for home screen adapter
class ChatMainRow : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout() =
        R.layout.chat_main_row
}