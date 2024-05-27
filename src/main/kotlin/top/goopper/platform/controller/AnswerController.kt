package top.goopper.platform.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.answer.AnswerQueryDTO
import top.goopper.platform.dto.answer.BatchCorrectAnswerDTO
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
     * 查询当前教师所有学生提交的作业
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping
    fun getSubmittedAnswers(
        @RequestParam corrected: Boolean?,
        @RequestParam groupId: Int?,
        @RequestParam courseId: Int?,
        @RequestParam sectionName: String,
        @RequestParam taskName: String,
        @RequestParam studentName: String,
        @RequestParam page: Int,
    ): ResponseEntity<Response> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val answers = answerService.getSubmittedAnswers(AnswerQueryDTO(
            teacherId = user.id,
            corrected = corrected,
            groupId = groupId,
            courseId = courseId,
            sectionName = sectionName,
            taskName = taskName,
            studentName = studentName,
            page = page
        ))
        return ResponseEntity.ok(Response.success(answers))
    }

    /**
     * 根据多个答案id获取答案id与任务名称
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/batch")
    fun getAnswerIdsAndTaskNames(
        @RequestParam answerIds: List<Int>
    ): ResponseEntity<Response> {
        val answerIdsAndTaskNames = answerService.getAnswerIdsAndTaskNames(answerIds)
        return ResponseEntity.ok(Response.success(answerIdsAndTaskNames))
    }

    /**
     * 根据作业id，获取学生提交的作业
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/{id}")
    fun getSubmittedAnswer(
        @PathVariable id: Int
    ): ResponseEntity<Response> {
        val answer = answerService.getSubmittedAnswer(id)
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

    /**
     * 教师批量批改作业
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/correct/batch")
    fun correctTasks(@RequestBody batchCorrectAnswerDTO: BatchCorrectAnswerDTO): ResponseEntity<Response> {
        answerService.correctTasks(batchCorrectAnswerDTO)
        return ResponseEntity.ok(Response.success())
    }

}