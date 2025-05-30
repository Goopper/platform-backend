package top.goopper.platform.dto.course.detail

import top.goopper.platform.dto.course.task.TaskDTO

data class SectionDetailDTO(
    val id: Int,
    val name: String,
    val desc: String,
    var tasks: List<TaskDTO>,
)
