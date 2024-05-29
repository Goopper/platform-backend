package top.goopper.platform.dto.answer

data class AnswerIdWithTaskNameDTO(
    val answerId: Int,
    val taskName: String,
    val corrected: Boolean,
)
