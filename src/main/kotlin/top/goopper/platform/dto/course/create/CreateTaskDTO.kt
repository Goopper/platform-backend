package top.goopper.platform.dto.course.create

import top.goopper.platform.dto.AttachmentDTO

data class CreateTaskDTO(
    val id: Long?,
    var sectionId: Long?,
    val name: String,
    val content: String,
    var attachments: List<AttachmentDTO>,
    val submitTypeId: Long,
)