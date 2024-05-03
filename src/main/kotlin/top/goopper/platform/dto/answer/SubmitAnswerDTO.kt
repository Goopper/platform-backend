package top.goopper.platform.dto.answer

import top.goopper.platform.dto.AttachmentDTO

data class SubmitAnswerDTO(
    val taskId: Int,
    val content: String?,
    val attachments: List<AttachmentDTO>
)
