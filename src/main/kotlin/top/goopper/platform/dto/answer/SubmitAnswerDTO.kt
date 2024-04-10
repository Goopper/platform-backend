package top.goopper.platform.dto.answer

import top.goopper.platform.dto.AttachmentDTO

data class SubmitAnswerDTO(
    val teacherId: Int,
    val courseId: Int,
    val sectionId: Int,
    val sectionName: String,
    val taskId: Int,
    val taskName: String,
    val content: String?,
    val typeId: Int,
    val attachments: List<AttachmentDTO>
)
