package top.goopper.platform.table

import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object StudentTask : Table<Nothing>("student_task") {

    val id = long("id").primaryKey()
    val studentId = long("student_id")
    val taskId = long("task_id")
    val messageId = long("message_id")
    val content = varchar("content")
    val comment = varchar("comment")
    val score = long("score")
    val createTime = date("create_time")
    val modifyTime = date("modify_time")

}