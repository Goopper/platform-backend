package top.goopper.platform.dto.course

data class TaskDTO(
    val id: Long,
    val name: String,
    val status: Boolean,
    val score: Long,
)
