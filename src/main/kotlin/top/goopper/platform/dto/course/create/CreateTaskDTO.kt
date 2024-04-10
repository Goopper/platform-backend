package top.goopper.platform.dto.course.create

import top.goopper.platform.dto.AttachmentDTO

data class CreateTaskDTO(
    val id: Int?,
    var sectionId: Int?,
    val name: String,
    val content: String,
    var attachments: List<AttachmentDTO>,
    val submitTypeId: Int,
)