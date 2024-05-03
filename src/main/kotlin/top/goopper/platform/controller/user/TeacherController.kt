package top.goopper.platform.controller.user

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.teacher.TeacherService

@RestController
@RequestMapping("/teacher")
class TeacherController(
    private val teacherService: TeacherService
) {

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping()
    fun getTeacherList(): ResponseEntity<Response> {
        val teachers = teacherService.getTeacherList()
        return ResponseEntity.ok(Response.success(teachers))
    }

}