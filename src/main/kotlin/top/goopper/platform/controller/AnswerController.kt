package top.goopper.platform.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.answer.CorrectAnswerDTO
import top.goopper.platform.dto.answer.SubmitAnswerDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.AnswerService

@RestController
@RequestMapping("/answer")
class AnswerController(
    private val answerService: AnswerService,
) {

    /**
     * 获取学生提交的作业
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/{messageId}")
    fun getSubmittedAnswer(
        @PathVariable messageId: Int
    ): ResponseEntity<Response> {
        val answer = answerService.getSubmittedAnswer(messageId)
        return ResponseEntity.ok(Response.success(answer))
    }

    /**
     * 学生提交作业
     */
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @PostMapping("/submit")
    fun submitTask(@RequestBody submitAnswerDTO: SubmitAnswerDTO): ResponseEntity<Response> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        answerService.submitTask(submitAnswerDTO, user.id)
        return ResponseEntity.ok(Response.success())
    }

    /**
     * 教师批改作业
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/correct")
    fun correctTask(@RequestBody correctAnswerDTO: CorrectAnswerDTO): ResponseEntity<Response> {
        answerService.correctTask(correctAnswerDTO)
        return ResponseEntity.ok(Response.success())
    }

}