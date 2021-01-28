package buildingThatApp.com.views

import android.widget.ImageView
import android.widget.TextView
import buildingThatApp.com.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatToItem(val text: String, private val userPhoto: String) : Item<GroupieViewHolder>() {
    // look up explanation in ChatFromItem
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textview_chat_right_to).text = text
        // going to load our image into image view
        val imageView =
            viewHolder.itemView.findViewById<ImageView>(R.id.current_user_chat_imageview)
        Picasso.get().load(userPhoto).into(imageView)
    }

    // This layout is going to be a row that renders out chat messages. here we are returning layout file  -
    // - for left_from perspective
    override fun getLayout() =
        R.layout.chat_right_to
}