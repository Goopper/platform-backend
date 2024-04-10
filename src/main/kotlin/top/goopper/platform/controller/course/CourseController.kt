package top.goopper.platform.controller.course

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import top.goopper.platform.dto.course.create.CreateCourseDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.course.CourseService

@RestController
@RequestMapping("/course")
class CourseController(
    private val courseService: CourseService,
) {

    /**
     * 创建课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    fun createNewCourse(
        @RequestBody course: CreateCourseDTO
    ): ResponseEntity<Response> {
        courseService.createNewCourse(course)
        return ResponseEntity.ok(Response.success())
    }

    /**
     * 获取课程草稿信息，包括附件
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/creation/{courseId}")
    fun getCreationInfo(
        @PathVariable courseId: Int
    ): ResponseEntity<Response> {
        val creation = courseService.getCreationInfo(courseId)
        return ResponseEntity.ok(Response.success(creation))
    }

    /**
     * 修改课程草稿
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping
    fun updateCourse(
        @RequestBody course: CreateCourseDTO,
    ): ResponseEntity<Response> {
        courseService.updateCourse(course)
        return ResponseEntity.ok(Response.success())
    }

    /**
     * 删除课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/{courseId}")
    fun deleteCourse(
        @PathVariable courseId: Int
    ): ResponseEntity<Response> {
        courseService.deleteCourse(courseId)
        return ResponseEntity.ok(Response.success())
    }

    /**
     * 获取课程详情信息
     */
    @GetMapping("/{courseId}")
    fun getCourseDetail(
        @PathVariable courseId: Int
    ): ResponseEntity<Response> {
        val course = courseService.getCourseDetail(courseId)
        return ResponseEntity.ok(Response.success(course))
    }

    /**
     * 获取课程结构，也就是章节，任务名称列表
     */
    @GetMapping("/tree/{courseId}")
    fun getCourseStructure(
        @PathVariable courseId: Int
    ): ResponseEntity<Response> {
        val structure = courseService.getCourseStructure(courseId)
        return ResponseEntity.ok(Response.success(structure))
    }

    /**
     * 获取当前教师创建的课程列表
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
    fun teacherGetCourseList(): ResponseEntity<Response> {
        val courseList = courseService.teacherGetCourseList()
        return ResponseEntity.ok(Response.success(courseList))
    }

    /**
     * 发布
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/publish/{courseId}")
    fun publishCourse(@PathVariable courseId: Int): ResponseEntity<Response> {
        courseService.publishCourse(courseId)
        return ResponseEntity.ok(Response.success())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/apply/{courseId}")
    fun applyCourseWithStudentInGroups(
        @PathVariable courseId: Int,
        @RequestParam groupIds: List<Int>
    ): ResponseEntity<Response> {
        courseService.applyCourseWithStudentInGroups(courseId, groupIds)
        return ResponseEntity.ok(Response.success())
    }

    /**
     * 学生手动选课
     */
    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping("/select/{courseId}")
    fun selectCourse(
        @PathVariable courseId: Int
    ): ResponseEntity<Response> {
        // 学生选课
        courseService.manualSelectCourse(courseId)
        return ResponseEntity.ok(Response.success())
    }

}