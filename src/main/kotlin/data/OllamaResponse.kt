package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OllamaFinalResponse(
    val model: String = "",
    @SerialName("created_at")
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
data class GenericMessage(
    val role: String = "",
    val content: String = ""
)

@Serializable
data class PromptWithHistory(
    val model: String = "",
    val messages: List<GenericMessage> = mutableListOf()
)

@Serializable
data class Chatroom(
    val id: Long = System.currentTimeMillis(),
    val title: String = "Test chatroom",
    val modelInThisChatroom: String? = null,
    val history: PromptWithHistory = PromptWithHistory()
)

@Serializable
data class ModelDetails(
    @SerialName("parent_model")
    val parentModel: String = "",
    val format: String = "",
    val family: String = "",
    val families: List<String>? = null,
    @SerialName("parameter_size")
    val parameterSize: String = "",
    @SerialName("quantization_level")
    val quantizationLevel: String = ""
)

@Serializable
data class OllamaModel(
    val name: String = "",
    val model: String = "",
    @SerialName("modified_at")
    val modifiedAt: String = "",
    val size: Long = 0,
    val digest: String = "",
    val details: ModelDetails = ModelDetails()
)

@Serializable
data class ModelsResponse(
    val models: List<OllamaModel> = emptyList()
)