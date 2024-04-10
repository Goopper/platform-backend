package top.goopper.platform.dao.task

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.AttachmentDTO
import top.goopper.platform.dto.course.TaskDTO
import top.goopper.platform.dto.course.create.CreateTaskDTO
import top.goopper.platform.dto.course.detail.TaskDetailDTO
import top.goopper.platform.table.answer.Answer
import top.goopper.platform.table.task.Task
import top.goopper.platform.table.task.TaskAttachment
import java.time.LocalDateTime

@Repository
class TaskDAO(private val database: Database) {

    fun createTask(createTaskDTO: CreateTaskDTO): Int {
        // insert task
        val taskId = database.insertAndGenerateKey(Task) {
            set(it.sectionId, createTaskDTO.sectionId)
            set(it.name, createTaskDTO.name)
            set(it.content, createTaskDTO.content)
            set(it.submitTypeId, createTaskDTO.submitTypeId)
        } as Int

        return taskId
    }

    fun loadTaskCreationInfo(taskId: Int): CreateTaskDTO {
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

    fun deleteTask(taskId: Int) {
        // delete task
        database.delete(Task) {
            it.id eq taskId
        }
        // delete task attachments relation
        database.delete(TaskAttachment) {
            it.taskId eq taskId
        }
    }

    fun studentLoadTasksBySectionId(sectionId: Int, studentId: Int): List<TaskDTO> {
        val tasks = database.from(Task)
            .leftJoin(Answer, Task.id eq Answer.taskId)
            .select()
            .where {
                (Task.sectionId eq sectionId) and (Answer.studentId eq studentId)
            }
            .map {
                TaskDTO(
                    id = it[Task.id]!!,
                    name = it[Task.name]!!,
                    status = it[Answer.score] != null,
                    score = it[Answer.score] ?: 0
                )
            }
        return tasks
    }

    fun teacherLoadTasksBySectionId(sectionId: Int): List<TaskDTO> {
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

    fun teacherLoadTaskDetail(taskId: Int): TaskDetailDTO {
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

    fun studentLoadTaskDetail(taskId: Int, uid: Int): TaskDetailDTO {
        val task = database.from(Task)
            .leftJoin(Answer, Task.id eq Answer.taskId)
            .select()
            .where {
                (Task.id eq taskId) and (Answer.studentId eq uid)
            }
            .map {
                TaskDetailDTO(
                    id = it[Task.id]!!,
                    name = it[Task.name]!!,
                    content = it[Task.content]!!,
                    status = it[Answer.score] != null,
                    score = it[Answer.score] ?: 0,
                    attachment = emptyList()
                )
            }
            .firstOrNull() ?: throw Exception("Task not found")
        return task
    }

}