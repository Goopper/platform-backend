package top.goopper.platform.dto.message

import top.goopper.platform.dto.UserDTO
import java.time.LocalDateTime

data class MessageListDTO(
    val id: Int,
    val title: String,
    val content: String,
    val typeId: Int,
    val sender: UserDTO,
    val date: LocalDateTime,
    val isRead: Boolean,
    val taskId: Int?
)
