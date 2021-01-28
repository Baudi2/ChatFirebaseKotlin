package buildingThatApp.com.views

import android.widget.ImageView
import android.widget.TextView
import buildingThatApp.com.R
import buildingThatApp.com.models.ChatMessage
import buildingThatApp.com.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

// viewHolder for home screen adapter
class ChatMainRow(private val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    lateinit var chatPartnerUser: User

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.message_textView_chat_main_row).text =
            chatMessage.text

        // before adding username into the text view we have figure out who is that "chat partner"
        val chatPartnerId: String = if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatMessage.toId
        } else {
            chatMessage.fromId
        }

        //getting access to the partners object
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // here we can take objects username and profile pic
                chatPartnerUser = snapshot.getValue(User::class.java) ?: return
                // that's how we get the username of a user that we are chatting with
                viewHolder.itemView.findViewById<TextView>(R.id.username_textView_chat_main_row).text =
                    chatPartnerUser.username

                // and now we'll get that user's profile image
                val imageView =
                    viewHolder.itemView.findViewById<ImageView>(R.id.user_image_chat_main_row)
                Picasso.get().load(chatPartnerUser.profileImageUrl).into(imageView)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun getLayout() =
        R.layout.chat_main_row
}