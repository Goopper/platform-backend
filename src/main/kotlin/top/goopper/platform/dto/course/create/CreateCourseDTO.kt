package top.goopper.platform.dto.course.create

import top.goopper.platform.dto.AttachmentDTO

data class CreateCourseDTO(
    val id: Int?,
    val name: String,
    val teacherId: Int,
    var attachments: List<AttachmentDTO>,
    val typeId: Int,
    val desc: String,
    val cover: String,
)