package top.goopper.platform.dto.course.create

data class CreateSectionDTO(
    val id: Long?,
    var courseId: Long?,
    val name: String,
    val desc: String,
)