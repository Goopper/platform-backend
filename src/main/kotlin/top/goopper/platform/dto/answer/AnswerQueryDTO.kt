package top.goopper.platform.dto.answer

data class AnswerQueryDTO(
    val teacherId: Int,
    val corrected: Boolean?,
    val groupId: Int?,
    val courseId: Int?,
    val sectionName: String,
    val taskName: String,
    val studentName: String,
    val page: Int,
    val pageSize: Int = 10
)