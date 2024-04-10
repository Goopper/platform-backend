package top.goopper.platform.table.task

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int

object TaskAttachment : Table<Nothing>("task_attachment") {

    val id = int("id").primaryKey()
    val taskId = int("task_id")
    val attachmentId = int("attachment_id")
    val createTime = datetime("create_time")

}