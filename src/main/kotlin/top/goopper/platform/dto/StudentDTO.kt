package top.goopper.platform.dto

data class StudentDTO(
    val id: Int,
    val number: Int,
    val name: String,
    val avatar: String,
    val finishedTask: Int,
    val totalTask: Int,
    var sectionName: String?,
    var taskName: String?,
    var lastUpdate: String?
)
