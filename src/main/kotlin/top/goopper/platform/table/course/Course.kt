package top.goopper.platform.table.course

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Course : Table<Nothing>("course") {

    val id = int("id").primaryKey()
    val name = varchar("name")
    val desc = varchar("desc")
    val cover = varchar("cover")
    val totalTask = int("total_task")
    val typeId = int("type_id")
    val teacherId = int("teacher_id")
    val statusId = int("status_id")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")
    val publishTime = datetime("publish_time")

}