package top.goopper.platform.dto.course

import top.goopper.platform.dto.course.task.TaskDTO

data class SectionDTO(
    val id: Int,
    val name: String,
    var tasks: List<TaskDTO>
)
