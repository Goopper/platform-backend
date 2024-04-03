package top.goopper.platform.table

import org.ktorm.schema.*

object Attachment : Table<Nothing>("attachment") {
    val id = long("id").primaryKey()
    val filename = varchar("filename")
    val originalFilename = varchar("original_filename")
    val url = varchar("url")
    val size = long("size")
    val type = varchar("type")
    val contentMD5 = varchar("content_md5")
    val createTime = datetime("create_time")
}