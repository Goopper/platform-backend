package top.goopper.platform.table

import org.ktorm.schema.*

object Task : Table<Nothing>("task") {

    val id = long("id").primaryKey()
    val sectionId = long("section_id")
    val submitTypeId = long("submit_type_id")
    val name = varchar("name")
    val content = varchar("content")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

}