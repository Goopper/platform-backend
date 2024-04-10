package top.goopper.platform.controller.course

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import top.goopper.platform.dto.course.create.CreateTaskDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.TaskService

@RestController
@RequestMapping("/task")
class TaskController(
    private val taskService: TaskService
) {

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/{sectionId}")
    fun createTask(
        @RequestBody createTaskDTO: CreateTaskDTO,
        @PathVariable sectionId: Int
    ): ResponseEntity<Response> {
        createTaskDTO.sectionId = sectionId
        taskService.createNewTask(createTaskDTO)
        return ResponseEntity.ok(Response.success())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/creation/{taskId}")
    fun loadTaskCreationInfo(
        @PathVariable taskId: Int
    ): ResponseEntity<Response> {
        val taskCreationInfo = taskService.loadTaskCreationInfo(taskId)
        return ResponseEntity.ok(Response.success(taskCreationInfo))
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping
    fun updateTask(
        @RequestBody createTaskDTO: CreateTaskDTO
    ): ResponseEntity<Response> {
        taskService.updateTask(createTaskDTO)
        return ResponseEntity.ok(Response.success())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/{taskId}")
    fun deleteTask(
        @PathVariable taskId: Int
    ): ResponseEntity<Response> {
        taskService.deleteTask(taskId)
        return ResponseEntity.ok(Response.success())
    }

    /**
     * 获取任务具体信息
     */
    @GetMapping("/{taskId}")
    fun getTaskDetail(
        @PathVariable taskId: Int
    ): ResponseEntity<Response> {
        val taskDetail = taskService.getTaskDetail(taskId)
        return ResponseEntity.ok(Response.success(taskDetail))
    }

}