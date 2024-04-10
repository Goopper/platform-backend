package top.goopper.platform.dto.course.detail

import top.goopper.platform.dto.AttachmentDTO

data class TaskDetailDTO(
    val id: Int,
    val name: String,
    val content: String,
    var status: Boolean,
    var score: Int,
    var attachment: List<AttachmentDTO>,
)
