package data

import kotlinx.serialization.Serializable


@Serializable
data class AppSettings(
    val darkMode: Boolean = false
)

@Serializable
data class Chatroom(
    val title: String = "Default title",
    val selectedModel: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val messages: List<Message> = emptyList()
)

@Serializable
data class Message(
    val isUserMessage: Boolean = false,
    val sentAt: Long = System.currentTimeMillis(),
    val message: String = "",
)