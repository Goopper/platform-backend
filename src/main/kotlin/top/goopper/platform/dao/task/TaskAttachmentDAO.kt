package top.goopper.platform.dao.task

import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import org.springframework.stereotype.Repository
import top.goopper.platform.table.task.TaskAttachment

@Repository
class TaskAttachmentDAO(
    private val database: Database
) {

    fun batchInsertTaskAttachment(taskId: Int, attachmentIds: List<Int>) {
        database.batchInsert(TaskAttachment) {
            attachmentIds.forEach { attachmentId ->
                item {
                    set(it.taskId, taskId)
                    set(it.attachmentId, attachmentId)
                }
            }
        }
    }

}