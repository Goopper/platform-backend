package top.goopper.platform.dto

import java.util.Date

data class MessageDTO(
    val id: Long?,
    val content: String,
    val sender: UserDTO,
    val receiver: UserDTO,
    val type: Long,
    val title: String,
    val sendDate: Date,
    val receiveDate: Date
)