package buildingThatApp.com

// the type of data that we are going to safe in our firebase db
data class User(
    val uid: String, val username: String, val profileImageUrl: String, val password: String
)