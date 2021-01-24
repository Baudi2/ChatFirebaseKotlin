package buildingThatApp.com.messages

import android.widget.ImageView
import android.widget.TextView
import buildingThatApp.com.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatFromItem(val text: String, val userPhoto : String) : Item<GroupieViewHolder>() {
    // To change the text inside the bubbles (message windows) we'll be using this method. Same in ChatToItem
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // getting access to the textView which display the message text. Same can be done in ChatToItem
        viewHolder.itemView.findViewById<TextView>(R.id.textview_chat_left_from).text = text
        // load photo of user to whom we are writing into the image view of that user
        val imageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageview_receiving_person)
        Picasso.get().load(userPhoto).into(imageView)
    }

    // This layout is going to be a row that renders out chat messages. here we are returning layout file  -
    // - for left_from perspective
    override fun getLayout() =
        R.layout.chat_left_from
}