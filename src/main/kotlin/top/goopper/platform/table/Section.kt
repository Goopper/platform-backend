package top.goopper.platform.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Section : Table<Nothing>("section") {

    val id = int("id").primaryKey()
    val courseId = int("course_id")
    val name = varchar("name")
    val desc = varchar("desc")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

}