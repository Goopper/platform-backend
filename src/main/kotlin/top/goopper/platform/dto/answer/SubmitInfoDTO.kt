package top.goopper.platform.dto.answer

data class SubmitInfoDTO(
    val studentId: Int,
    val teacherId: Int,
    val courseId: Int,
    val submitTypeId: Int,
    val taskName: String,
    val sectionName: String,
    val sectionId: Int,
)
