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
    val messages: List<Chat> = emptyList()
)

@Serializable
data class Chat(
    val userMessage: String = "",
    val modelMessage: String = ""
)