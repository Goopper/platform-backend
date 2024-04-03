package top.goopper.platform.dto.course

data class SectionDTO(
    val id: Long,
    val name: String,
    var tasks: List<TaskDTO>
)
