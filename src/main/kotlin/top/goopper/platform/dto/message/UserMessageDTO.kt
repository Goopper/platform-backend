package top.goopper.platform.dto.message

import java.time.LocalDateTime

data class UserMessageDTO(
    val senderId: Int,
    val receiverId: Int,
    val messageId: Int,
    val answerId: Int? = null,
    val readTime: LocalDateTime? = null,
)