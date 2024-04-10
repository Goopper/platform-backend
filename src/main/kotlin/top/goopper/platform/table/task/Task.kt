package top.goopper.platform.table.task

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

open class Task(alias: String?) : Table<Nothing>("task", alias) {
    companion object : Task(null)

    val id = int("id").primaryKey()
    val sectionId = int("section_id")
    val submitTypeId = int("submit_type_id")
    val name = varchar("name")
    val content = varchar("content")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

    override fun aliased(alias: String) = Task(alias)
}