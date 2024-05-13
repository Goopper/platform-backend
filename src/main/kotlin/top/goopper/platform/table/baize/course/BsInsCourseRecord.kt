package top.goopper.platform.table.baize.course

import org.ktorm.schema.*

object BsInsCourseRecord : Table<Nothing>("bs_ins_course_record") {

    val id = long("id").primaryKey()
    val recordId = varchar("record_id")
    val courseId = varchar("course_id")
    val taskId = varchar("task_id")
    val recordType = varchar("record_type")
    val groupName = varchar("group_name")
    val groupId = varchar("group_id")
    val title = varchar("title")
    val startTime = datetime("start_time")
    val endTime = datetime("end_time")
    val isLock = varchar("is_lock")
    val progressPercent = int("progress_precent")
    val stuProgressPercent = int("stu_progress_precent")
    val status = varchar("status")
    val sort = int("sort")
    val teacherId = long("teacher_id")
    val teachBy = varchar("teach_by")
    val createBy = varchar("create_by")
    val createTime = datetime("create_time")
    val updateBy = varchar("update_by")
    val updateTime = datetime("update_time")

}