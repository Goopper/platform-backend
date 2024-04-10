package top.goopper.platform.dto.answer

import top.goopper.platform.dto.AttachmentDTO

data class AnswerDTO(
    val answerId: Int,
    val studentName: String,
    val groupName: String,
    val taskName: String,
    val content: String,
    var attachments: List<AttachmentDTO>,
    var nextAnswerTaskName: String? = null,
    var nextAnswerStudentName: String? = null,
    var nextAnswerGroupName: String? = null,
    // This is user_message! NOT message!!!
    var nextAnswerUserMessageId: Int? = null
)
