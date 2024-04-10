package top.goopper.platform.dto.message

import top.goopper.platform.dto.UserDTO
import java.util.*

data class MessageDetailDTO(
    val id: Int?,
    val content: String,
    val sender: UserDTO,
    val receiver: UserDTO,
    val type: Int,
    val title: String,
    val sendDate: Date,
    val receiveDate: Date
)