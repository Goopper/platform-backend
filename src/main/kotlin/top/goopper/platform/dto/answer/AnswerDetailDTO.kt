package top.goopper.platform.dto.answer

import top.goopper.platform.dto.AttachmentDTO

data class AnswerDetailDTO(
    val id: Int,
    val taskContent: String,
    val answerContent: String,
    var attachments: List<AttachmentDTO>,
    val answer: AnswerDTO
)
