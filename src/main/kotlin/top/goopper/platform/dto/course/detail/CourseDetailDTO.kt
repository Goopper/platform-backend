package top.goopper.platform.dto.course.detail

import top.goopper.platform.dto.AttachmentDTO

data class CourseDetailDTO(
    val id: Long,
    val name: String,
    val teacher: String,
    val type: String,
    val desc: String,
    val cover: String,
    val totalTask: Long,
    var attachments: List<AttachmentDTO>
)