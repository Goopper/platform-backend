package top.goopper.platform.table.student

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int

object StudentCourse : Table<Nothing>("student_course") {

    val id = int("id").primaryKey()
    val studentId = int("student_id")
    val courseId = int("course_id")
    val finishedTask = int("finished_task")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

}