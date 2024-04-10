package top.goopper.platform.dto.message

data class MessageDTO(
    val title: String,
    val content: String,
    val typeId: Int,
    val senderId: Int,
    val receiverId: Int,
)
