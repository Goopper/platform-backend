package top.goopper.platform.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long

object CourseAttachment : Table<Nothing>("course_attachment") {

    val id = long("id").primaryKey()
    val courseId = long("course_id")
    val attachmentId = long("attachment_id")
    val createTime = datetime("create_time")

}