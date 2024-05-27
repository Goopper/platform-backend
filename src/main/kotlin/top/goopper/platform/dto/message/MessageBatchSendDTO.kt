package top.goopper.platform.dto.message

data class MessageBatchSendDTO(
    val title: String,
    val content: String,
    val typeId: Int,
    val senderId: Int,
    val receiverIds: List<Int>,
)
