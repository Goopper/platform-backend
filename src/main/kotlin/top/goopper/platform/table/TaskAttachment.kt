package top.goopper.platform.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long

object TaskAttachment : Table<Nothing>("task_attachment") {

    val id = long("id").primaryKey()
    val taskId = long("task_id")
    val attachmentId = long("attachment_id")
    val createTime = datetime("create_time")

}