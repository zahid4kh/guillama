package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OllamaFinalResponse(
    val model: String = "",
    val createdAt: String = "",
    val message: OllamaMessage = OllamaMessage(),
    val done: Boolean = false,
    @SerialName("total_duration")
    val totalDuration: Long = 0,
    @SerialName("load_duration")
    val loadDuration: Long = 0,
    @SerialName("prompt_eval_count")
    val promptEvalCount: Int = 0,
    @SerialName("prompt_eval_duration")
    val promptEvalDuration: Long = 0,
    @SerialName("eval_count")
    val evalCount: Int = 0,
    @SerialName("eval_duration")
    val evalDuration: Long = 0
)

@Serializable
data class OllamaMessage(
    val role: String = "assistant",
    val content: String = ""
)

@Serializable
data class OllamaStreamResponse(
    val model: String = "",
    val createdAt: String = "",
    val message: OllamaMessage = OllamaMessage(),
    val done: Boolean = false,
)

@Serializable
data class UserMessage(
    val role: String = "user",
    val content: String = ""
)

@Serializable
data class GenericMessage(
    val role: String = "",
    val content: String = ""
)

@Serializable
data class PromptWithHistory(
    val model: String = "gemma3:4b",
    val messages: List<GenericMessage> = emptyList()
)