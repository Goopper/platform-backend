package top.goopper.platform.table

import org.ktorm.schema.*

object Section : Table<Nothing>("section") {

    val id = long("id").primaryKey()
    val courseId = long("course_id")
    val name = varchar("name")
    val desc = varchar("desc")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

}