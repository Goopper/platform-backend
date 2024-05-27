package top.goopper.platform.dto.answer

import java.time.LocalDateTime

data class AnswerDTO(
    val id: Int,
    val number: Int,
    val studentName: String,
    val groupName: String,
    val courseName: String,
    val sectionName: String,
    val taskName: String,
    val corrected: Boolean,
    val submitTime: LocalDateTime,
)
