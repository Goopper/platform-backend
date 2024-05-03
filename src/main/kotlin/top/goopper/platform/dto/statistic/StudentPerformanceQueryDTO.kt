package top.goopper.platform.dto.statistic

data class StudentPerformanceQueryDTO(
    val studentName: String? = null,
    val groupId: Int? = null,
    val courseTypeId: Int? = null,
    val page: Int = 1,
    var teacherId: Int? = null,
)
