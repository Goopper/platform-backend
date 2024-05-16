package top.goopper.platform.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dao.AttachmentDAO
import top.goopper.platform.dao.task.TaskAttachmentDAO
import top.goopper.platform.dao.task.TaskDAO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.course.create.CreateTaskDTO
import top.goopper.platform.dto.course.detail.TaskDetailDTO
import top.goopper.platform.dto.course.task.SubmitTypeListDTO
import top.goopper.platform.enum.RoleEnum

@Service
class TaskService(
    private val taskDAO: TaskDAO,
    private val attachmentDAO: AttachmentDAO,
    private val taskAttachmentDAO: TaskAttachmentDAO
) {

    @Transactional(rollbackFor = [Exception::class], transactionManager = "basicTransactionManager")
    fun createNewTask(createTaskDTO: CreateTaskDTO) {
        // create attachment and fill the id
        attachmentDAO.batchCreateAttachment(createTaskDTO.attachments)
        // create task
        val taskId = taskDAO.createTask(createTaskDTO)
        // table course will auto increment totalTask by 1 when task is created
        // because of the trigger in database `task_insert_trigger`
        if (createTaskDTO.attachments.isEmpty()) return
        taskAttachmentDAO.batchInsertTaskAttachment(taskId, createTaskDTO.attachments.map { it.id!! })
    }

    // load task creation info
    fun loadTaskCreationInfo(taskId: Int): CreateTaskDTO {
        val creation = taskDAO.loadTaskCreationInfo(taskId)
        creation.attachments = attachmentDAO.loadTaskAttachments(taskId)
        return creation
    }

    /**
     * Modify course info, will check the attachment conflict and append new attachments
     * conflict attachment: attachment not deleted by user
     */
    @Transactional(rollbackFor = [Exception::class], transactionManager = "basicTransactionManager")
    fun updateTask(createTaskDTO: CreateTaskDTO) {
        // update attachment
        val newAttachments = createTaskDTO.attachments.filter {
            it.id == null
        }
        // create attachment and fill the id
        attachmentDAO.batchCreateAttachment(newAttachments)
        // update task
        taskDAO.updateTask(createTaskDTO, newAttachments)
        // add relation between task and attachment
        if (newAttachments.isEmpty()) return
        taskAttachmentDAO.batchInsertTaskAttachment(createTaskDTO.id!!, newAttachments.map { it.id!! })
    }

    /**
     * schedule task will be deleted unused attachment (db and files)
     * @see top.goopper.platform.schedule.CleanAttachmentTask
     */
    @Transactional(rollbackFor = [Exception::class], transactionManager = "basicTransactionManager")
    fun deleteTask(taskId: Int) {
        // delete task
        taskDAO.deleteTask(taskId)
        // table course will auto decrement totalTask by 1 when task is deleted
        // because of the trigger in database `task_delete_trigger`
    }

    fun getTaskDetail(taskId: Int): TaskDetailDTO {
        val task: TaskDetailDTO
        val auth = SecurityContextHolder.getContext().authentication
        val role = auth.authorities.first().authority
        // check roles
        if (role == "ROLE_${RoleEnum.STUDENT.name}") {
            // load status
            val uid = (auth.principal as UserDTO).id
            task = taskDAO.studentLoadTaskDetail(taskId, uid)
        } else {
            task = taskDAO.teacherLoadTaskDetail(taskId)
        }
        task.attachment = attachmentDAO.loadTaskAttachments(taskId)
        return task
    }

    fun deleteTaskAttachment(taskId: Int, attachmentId: Int) {
        taskAttachmentDAO.deleteTaskAttachment(taskId, attachmentId)
    }

    fun getSubmitTypeList(): List<SubmitTypeListDTO> {
        val types = taskDAO.loadSubmitTypeList()
        return types
    }

}