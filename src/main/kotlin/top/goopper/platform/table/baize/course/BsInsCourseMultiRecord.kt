package top.goopper.platform.table.baize.course

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object BsInsCourseMultiRecord : Table<Nothing>("bs_ins_course_multi_record") {

    val id = varchar("id").primaryKey()
    val expId = varchar("exp_id")
    val expNum = varchar("exp_num")
    val versionNum = varchar("version_num")
    val expTitle = varchar("exp_title")
    val resourceId = varchar("resource_id")
    val relateVersionId = varchar("relate_version_id")
    val lastMultiRelateId = varchar("last_multi_relate_id")
    val status = varchar("status")
    val sort = int("sort")
    val createBy = varchar("create_by")
    val createTime = datetime("create_time")
    val updateBy = varchar("update_by")
    val updateTime = datetime("update_time")

}