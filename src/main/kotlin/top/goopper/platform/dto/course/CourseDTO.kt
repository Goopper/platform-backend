package top.goopper.platform.dto.course

data class CourseDTO(
    val id: Int,
    val name: String,
    val cover: String,
    val type: String,
    val desc: String,
    val status: String
)