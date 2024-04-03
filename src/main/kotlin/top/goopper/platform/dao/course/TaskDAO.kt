package top.goopper.platform.dao.course

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import top.goopper.platform.dto.AttachmentDTO
import top.goopper.platform.dto.course.TaskDTO
import top.goopper.platform.dto.course.create.CreateTaskDTO
import top.goopper.platform.dto.course.detail.TaskDetailDTO
import top.goopper.platform.table.StudentTask
import top.goopper.platform.table.Task
import top.goopper.platform.table.TaskAttachment
import java.time.LocalDateTime

@Component
class TaskDAO(private val database: Database) {

    fun createTask(createTaskDTO: CreateTaskDTO): Long {
        // insert task
        val taskId = database.insertAndGenerateKey(Task) {
            set(it.sectionId, createTaskDTO.sectionId)
            set(it.name, createTaskDTO.name)
            set(it.content, createTaskDTO.content)
            set(it.submitTypeId, createTaskDTO.submitTypeId)
        } as Long

        return taskId
    }

    fun loadTaskCreationInfo(taskId: Long): CreateTaskDTO {
        val creation = database.from(Task)
            .select()
            .where(Task.id eq taskId)
            .map {
                CreateTaskDTO(
                    id = taskId,
                    sectionId = it[Task.sectionId]!!,
                    name = it[Task.name]!!,
                    content = it[Task.content]!!,
                    submitTypeId = it[Task.submitTypeId]!!,
                    attachments = emptyList()
                )
            }.firstOrNull() ?: throw Exception("Task not found")
        return creation
    }

    /**
     * Modify course info, will check the attachment conflict and append new attachments
     * conflict attachment: attachment not deleted by user
     */
    fun updateTask(createTaskDTO: CreateTaskDTO, newAttachments: List<AttachmentDTO>) {
        database.update(Task) {
            // TODO duplicate code
            set(it.sectionId, createTaskDTO.sectionId)
            set(it.name, createTaskDTO.name)
            set(it.content, createTaskDTO.content)
            set(it.submitTypeId, createTaskDTO.submitTypeId)
            set(it.modifyTime, LocalDateTime.now())
            where {
                it.id eq createTaskDTO.id!!
            }
        }
    }

    fun deleteTask(taskId: Long) {
        // delete task
        database.delete(Task) {
            it.id eq taskId
        }
        // delete task attachments relation
        database.delete(TaskAttachment) {
            it.taskId eq taskId
        }
    }

    fun studentLoadTasksBySectionId(sectionId: Long, studentId: Long): List<TaskDTO> {
        val tasks = database.from(Task)
            .leftJoin(StudentTask, Task.id eq StudentTask.taskId)
            .select()
            .where {
                (Task.sectionId eq sectionId) and (StudentTask.studentId eq studentId)
            }
            .map {
                TaskDTO(
                    id = it[Task.id]!!,
                    name = it[Task.name]!!,
                    status = it[StudentTask.score] != null,
                    score = it[StudentTask.score] ?: 0
                )
            }
        return tasks
    }

    fun teacherLoadTasksBySectionId(sectionId: Long): List<TaskDTO> {
        val tasks = database.from(Task)
            .select()
            .where {
                Task.sectionId eq sectionId
            }
            .map {
                TaskDTO(
                    id = it[Task.id]!!,
                    name = it[Task.name]!!,
                    status = false,
                    score = 0
                )
            }
        return tasks
    }

    fun teacherLoadTaskDetail(taskId: Long): TaskDetailDTO {
        val task = database.from(Task)
            .select()
            .where(Task.id eq taskId)
            .map {
                TaskDetailDTO(
                    id = it[Task.id]!!,
                    name = it[Task.name]!!,
                    content = it[Task.content]!!,
                    status = false,
                    score = 0,
                    attachment = emptyList()
                )
            }
            .firstOrNull() ?: throw Exception("Task not found")
        return task
    }

    fun studentLoadTaskDetail(taskId: Long, uid: Long): TaskDetailDTO {
        val task = database.from(Task)
            .leftJoin(StudentTask, Task.id eq StudentTask.taskId)
            .select()
            .where {
                (Task.id eq taskId) and (StudentTask.studentId eq uid)
            }
            .map {
                TaskDetailDTO(
                    id = it[Task.id]!!,
                    name = it[Task.name]!!,
                    content = it[Task.content]!!,
                    status = it[StudentTask.score] != null,
                    score = it[StudentTask.score] ?: 0,
                    attachment = emptyList()
                )
            }
            .firstOrNull() ?: throw Exception("Task not found")
        return task
    }

}