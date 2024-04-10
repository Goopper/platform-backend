package top.goopper.platform.dto.course

data class SectionDTO(
    val id: Int,
    val name: String,
    var tasks: List<TaskDTO>
)
