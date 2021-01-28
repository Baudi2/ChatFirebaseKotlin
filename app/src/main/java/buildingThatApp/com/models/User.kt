package buildingThatApp.com.models

// the type of data that we are going to safe in our firebase db
data class User(
    val uid: String, val username: String, val profileImageUrl: String, val password: String
) {
    // we need to define empty constructor for getting data from firebase db purposes
    constructor() : this("", "", "", "")
}