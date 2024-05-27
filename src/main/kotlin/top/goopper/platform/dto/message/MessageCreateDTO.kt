package top.goopper.platform.dto.message

data class MessageCreateDTO (
    val title: String,
    val content: String,
    val typeId: Int,
)