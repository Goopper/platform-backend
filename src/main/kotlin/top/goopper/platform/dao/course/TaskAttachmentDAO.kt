package top.goopper.platform.dao.course

import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import org.springframework.stereotype.Component
import top.goopper.platform.table.TaskAttachment

@Component
class TaskAttachmentDAO(
    private val database: Database
) {

    fun batchInsertTaskAttachment(taskId: Long, attachmentIds: List<Long>) {
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