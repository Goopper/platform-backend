package top.goopper.platform.controller.user

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.StudentService

@RestController
@RequestMapping("/student")
class StudentController(
    private val studentService: StudentService
) {

    /**
     * 获取学习中的课程简要信息列表
     *
     * 如果没有进行任何学习，那么返回的数据中的进度信息为空
     */
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/course/current/all")
    fun getCurrentLearningCourse(): ResponseEntity<Response> {
        val courses = studentService.getCurrentLearningCourseList()
        return ResponseEntity.ok(Response.success(courses))
    }

    /**
     * 获取可选课程列表，可选课程
     * 1. 未开始学习 (未加入)
     * 2. 已发布
     */
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/course")
    fun getAvailableCourse(): ResponseEntity<Response> {
        val courses = studentService.getAvailableCourse()
        return ResponseEntity.ok(Response.success(courses))
    }

    /**
     * 获取最近学习的一个课程（最近学习的判断依据是记录修改时间）
     *
     * 如果没有进行任何学习，那么返回的数据中的进度信息为空
     * @throws Exception 学生未选择任何课程
     */
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/course/current")
    fun getLatestLearningCourse(): ResponseEntity<Response> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val course = studentService.getLatestLearningCourse(user.id)
        return ResponseEntity.ok(Response.success(course))
    }

    /**
     * 教师根据学生ID获取最近学习的一个课程（最近学习的判断依据是记录修改时间）
     *
     * 如果没有进行任何学习，那么返回的数据中的进度信息为空
     * @throws Exception 学生未选择任何课程
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/course/current/{studentId}")
    fun getLatestLearningCourse(
        @PathVariable studentId: Int
    ): ResponseEntity<Response> {
        val course = studentService.getLatestLearningCourse(studentId)
        return ResponseEntity.ok(Response.success(course))
    }

    /**
     * 教师获取指定学生的指定课程的完成情况
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/course/{studentId}/{courseId}")
    fun getCourseLearningProgressDetail(
        @PathVariable studentId: Int,
        @PathVariable courseId: Int
    ): ResponseEntity<Response> {
        val sections = studentService.getCourseLearningProgressDetail(studentId, courseId)
        return ResponseEntity.ok(Response.success(sections))
    }

    /**
     * 教师根据课程与小组批量获取学生的学习进度，也就是首页的学生列表
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/{courseId}/{groupId}/{orderId}")
    fun getStudentLearningProgress(
        @PathVariable courseId: Int,
        @PathVariable groupId: Int,
        @PathVariable orderId: Int
    ): ResponseEntity<Response> {
        val progress = studentService.getStudentsLearningProgress(courseId, groupId, orderId)
        return ResponseEntity.ok(Response.success(progress))
    }

    /**
     * 学生个人获取学生学习统计年份列表
     */
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/statistic")
    fun getStudentStatisticYearsList(): ResponseEntity<Response> {
        val years = studentService.getStudentStatisticYears()
        return ResponseEntity.ok(Response.success(years))
    }

    /**
     * 学生个人获取学生全年学习统计
     */
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/statistic/{year}")
    fun getFullYearStudentStatistic(@PathVariable year: Int): ResponseEntity<Response> {
        val statistic = studentService.getStudentFullYearStatistic(year)
        return ResponseEntity.ok(Response.success(statistic))
    }

}