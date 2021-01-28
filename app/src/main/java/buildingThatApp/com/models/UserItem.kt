package buildingThatApp.com.models

import android.widget.TextView
import buildingThatApp.com.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

// this class extends Groupie Item class. We need this in order to later add this class in to our adapter to display it
// in here we have to override two methods.
// изменяем немного этот класс, добавляем параметр в конструктор, это класс user который мы получаем через fetchUsers from firebase db
class UserItem(val user: User) : Item<GroupieViewHolder>() {
    // Called in our list for each object. Similar to onBindViewHolder which we use in regular custom adapter class
    // I guess this method fills individual objects or rows with data.
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // we are going to bind the data with ui. Unfortunately here we'll have to use findViewById() since -
        // - user_row_new_message layout doesn't have any fragment to attach to it therefore doesn't have binding property
        // here we basically add the username value which fetch from firebase to our single item view layout to later fill with it recyclerView
        viewHolder.itemView.findViewById<TextView>(R.id.username_text_view_new_message).text =
            user.username

        // we'll use Picasso to load the images. First we specify the uri that we'll use, then we say where we'll load this image
        Picasso.get().load(user.profileImageUrl).into(
            viewHolder.itemView.findViewById<CircleImageView>(
                R.id.circle_image_new_message
            )
        )
    }

    // This method will going to "render" each rows of items inside our recyclerView. Similar to what viewHolder does in a regular scenario
    // We use here Kotlin's short syntax for return. We have pass here a layout file for individual items which will fill recyclerView.
    override fun getLayout() =
        R.layout.user_row_new_message
}