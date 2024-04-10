package top.goopper.platform.controller.statis

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.dto.statistic.StudentPerformanceQueryDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.StatisticService

@RestController
@RequestMapping("/statistic")
class StatisticController(
    private val statisticService: StatisticService
) {

    /**
     * 获取学生表现，课程学习进度信息
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/performance/student")
    fun getStudentPerformance(
        @RequestParam studentName: String? = null,
        @RequestParam groupId: Int? = null,
        @RequestParam courseTypeId: Int? = null,
        @RequestParam page: Int? = null
    ): ResponseEntity<Response> {
        val performance = statisticService.getStudentPerformance(
            StudentPerformanceQueryDTO(
                studentName = studentName,
                groupId = groupId,
                courseTypeId = courseTypeId,
                page = page
            )
        )
        return ResponseEntity.ok(Response.success(performance))
    }

}