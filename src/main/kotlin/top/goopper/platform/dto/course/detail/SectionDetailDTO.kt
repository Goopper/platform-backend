package top.goopper.platform.dto.course.detail

import top.goopper.platform.dto.course.TaskDTO

data class SectionDetailDTO(
    val id: Long,
    val name: String,
    val desc: String,
    var tasks: List<TaskDTO>,
)
