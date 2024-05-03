package top.goopper.platform.dto.statistic

data class StudentPerformancePageDTO(
    val page: Int,
    val totalPage: Int,
    val total: Int,
    val list: List<StudentPerformanceDTO>
)
