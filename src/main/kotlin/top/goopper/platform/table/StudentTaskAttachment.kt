package top.goopper.platform.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long

object StudentTaskAttachment : Table<Nothing>("student_task_attachment") {
    val id = long("id").primaryKey()
    val studentTaskId = long("student_task_id")
    val attachmentId = long("attachment_id")
    val createTime = datetime("create_time")
}