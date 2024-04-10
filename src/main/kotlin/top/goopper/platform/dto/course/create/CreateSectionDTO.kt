package top.goopper.platform.dto.course.create

data class CreateSectionDTO(
    val id: Int?,
    var courseId: Int?,
    val name: String,
    val desc: String,
)