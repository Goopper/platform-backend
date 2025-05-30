package top.goopper.platform.dto.statistic

data class StudentPerformanceDTO(
    val id: Int,
    val studentId: Int,
    val studentNumber: Int,
    val name: String,
    val groupName: String,
    val courseName: String,
    val finishedTask: Int,
    val totalTask: Int,
)
