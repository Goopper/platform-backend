package top.goopper.platform.dto.course.create

import top.goopper.platform.dto.AttachmentDTO

data class CreateCourseDTO(
    val id: Long?,
    val name: String,
    val teacherId: Long,
    var attachments: List<AttachmentDTO>,
    val typeId: Long,
    val desc: String,
    val cover: String,
)