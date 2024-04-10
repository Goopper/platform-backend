package top.goopper.platform.dto.course.progress

import top.goopper.platform.dto.course.CourseDTO

data class StudentLearningProgressDTO(
    val id: Int,
    val course: CourseDTO,
    var lastSectionName: String? = null,
    var lastTaskName: String? = null,
    var lastLearningDate: String? = null,
    val finishedTask: Int,
    val totalTask: Int,
)
