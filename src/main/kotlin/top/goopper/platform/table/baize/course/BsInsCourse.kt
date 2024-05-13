package top.goopper.platform.table.baize.course

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object BsInsCourse : Table<Nothing>("bs_ins_course") {

    val id = varchar("id").primaryKey()
    val courseId = varchar("course_id")
    val courseTitle = varchar("course_title")
    val courseDesc = varchar("course_desc")
    val courseLogo = varchar("course_logo")
    val status = varchar("status")
    val scoreCourseId = varchar("score_course_id")
    val sort = int("sort")
    val createBy = varchar("create_by")
    val createTime = datetime("create_time")
    val updateBy = varchar("update_by")
    val updateTime = datetime("update_time")

}