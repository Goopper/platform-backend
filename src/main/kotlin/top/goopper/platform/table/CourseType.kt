package top.goopper.platform.table

import org.ktorm.schema.*

object CourseType : Table<Nothing>("course_type") {

    val id = long("id").primaryKey()
    val name = varchar("name")
    val createTime = datetime("create_time")

}