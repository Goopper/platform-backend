package top.goopper.platform.dto.message

data class MessagePageDTO(
    val page: Int,
    val totalPage: Int,
    val total: Int,
    val list: List<MessageListDTO>
)
