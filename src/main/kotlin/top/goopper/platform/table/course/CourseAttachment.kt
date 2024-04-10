package top.goopper.platform.table.course

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int

object CourseAttachment : Table<Nothing>("course_attachment") {

    val id = int("id").primaryKey()
    val courseId = int("course_id")
    val attachmentId = int("attachment_id")
    val createTime = datetime("create_time")

}