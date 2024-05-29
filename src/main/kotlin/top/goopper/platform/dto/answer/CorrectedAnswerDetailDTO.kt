package top.goopper.platform.dto.answer

import top.goopper.platform.dto.AttachmentDTO

data class CorrectedAnswerDetailDTO(
    val id: Int,
    val taskContent: String,
    val answerContent: String,
    var attachments: List<AttachmentDTO>,
    val answer: AnswerDTO,
    val comment: String,
    val score: Int,
)
