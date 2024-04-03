package top.goopper.platform.controller.course

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.dto.course.create.CreateCourseDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.course.CourseService

@RestController
@RequestMapping("/course")
class CourseController(
    private val courseService: CourseService,
) {

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    fun createNewCourse(
        @RequestBody course: CreateCourseDTO
    ): ResponseEntity<Response> {
        courseService.createNewCourse(course)
        return ResponseEntity.ok(Response.success())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/creation/{courseId}")
    fun getCreationInfo(
        @PathVariable courseId: Long
    ): ResponseEntity<Response> {
        val creation = courseService.getCreationInfo(courseId)
        return ResponseEntity.ok(Response.success(creation))
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping
    fun updateCourse(
        @RequestBody course: CreateCourseDTO,
    ): ResponseEntity<Response> {
        courseService.updateCourse(course)
        return ResponseEntity.ok(Response.success())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/{courseId}")
    fun deleteCourse(
        @PathVariable courseId: Long
    ): ResponseEntity<Response> {
        courseService.deleteCourse(courseId)
        return ResponseEntity.ok(Response.success())
    }

    @GetMapping("/{courseId}")
    fun getCourseDetail(
        @PathVariable courseId: Long
    ): ResponseEntity<Response> {
        val course = courseService.getCourseDetail(courseId)
        return ResponseEntity.ok(Response.success(course))
    }

    @GetMapping("/tree/{courseId}")
    fun getCourseStructure(
        @PathVariable courseId: Long
    ): ResponseEntity<Response> {
        val structure = courseService.getCourseStructure(courseId)
        return ResponseEntity.ok(Response.success(structure))
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
    fun teacherGetCourseList(): ResponseEntity<Response> {
        val courseList = courseService.teacherGetCourseList()
        return ResponseEntity.ok(Response.success(courseList))
    }

}