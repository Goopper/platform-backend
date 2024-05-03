package top.goopper.platform.dto.course.task

data class TaskDTO(
    val id: Int,
    val name: String,
    val status: Boolean,
    val score: Int?,
)
