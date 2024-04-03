package top.goopper.platform.table

import org.ktorm.schema.*

object Course : Table<Nothing>("course") {

    val id = long("id").primaryKey()
    val name = varchar("name")
    val desc = varchar("desc")
    val cover = varchar("cover")
    val totalTask = long("total_task")
    val typeId = long("type_id")
    val teacherId = long("teacher_id")
    val statusId = long("status_id")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")
    val publishTime = datetime("publish_time")

}