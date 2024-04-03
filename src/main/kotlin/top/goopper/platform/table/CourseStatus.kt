package top.goopper.platform.table

import org.ktorm.schema.*

object CourseStatus: Table<Nothing>("course_status") {

    val id = long("id").primaryKey()
    val name = varchar("name")
    val createTime = datetime("create_time")

}