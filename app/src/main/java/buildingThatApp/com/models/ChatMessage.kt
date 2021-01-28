package buildingThatApp.com.models

data class ChatMessage(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val timestamp: Long
) {
    // we need to provide this empty constructor since app keeps crushing if we don't do that
    constructor() : this("", "", "", "", -1)
}
