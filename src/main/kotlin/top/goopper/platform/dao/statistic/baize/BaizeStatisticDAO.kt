package top.goopper.platform.dao.statistic.baize

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.statistic.baize.RecentContainerInfoDTO
import top.goopper.platform.table.baize.course.BsInsCourse
import top.goopper.platform.table.baize.course.BsInsCourseMultiRecord
import top.goopper.platform.table.baize.course.BsInsCourseRecord
import java.text.SimpleDateFormat



@Repository
class BaizeStatisticDAO(
    @Qualifier("analyticalDB") private val analyticalDB: Database
) {

    var sdf: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    /**
     * 获取课程教学实验记录
     */
    fun getCourseTeachCountByCourseTitle(): List<List<Any>> {
        val count = count(BsInsCourse.courseTitle).aliased("count")
        val query = analyticalDB.from(BsInsCourseRecord)
            .innerJoin(BsInsCourse, BsInsCourseRecord.courseId eq BsInsCourse.courseId)
            .innerJoin(BsInsCourseMultiRecord, BsInsCourseMultiRecord.resourceId eq BsInsCourse.courseId)
            .select(
                // course
                BsInsCourse.courseTitle,
                // count
                count
            )
            .where {
                // record type is course
                BsInsCourseRecord.recordType eq "course"
            }
            .groupBy(BsInsCourse.courseTitle)
        val res = query.map {
            listOf(it[BsInsCourse.courseTitle]!!, it[count]!!)
        }
        return res
    }

    /**
     * 获取容器开启时长前number的学生
     */
    fun getContainerOpenDurationTop(number: Int): List<List<Any>> {
        analyticalDB.useConnection {
            val statement = it.prepareStatement(
                """
                    select s.student_name,
                           SUM((t.endtime - t.completetime) / 60 / 60) as totaltime
                    from cc_ins_container t
                    inner join `baize_5.0`.bs_ins_stu_group_member s on t.user_name = s.account
                    where t.user_id != t.teach_by_id
                    group by user_id order by totaltime desc LIMIT ?;
                """.trimIndent()
            )
            statement.setInt(1, number)
            val resultSet = statement.executeQuery()
            val res = mutableListOf<List<Any>>()
            while (resultSet.next()) {
                res.add(listOf(resultSet.getString(1), resultSet.getInt(2)))
            }
            return res
        }
    }

    /**
     * 获取学生课程完成情况(已完成，未完成)
     */
    fun getStuCourseFinishedStatus(): List<List<Any>> {
        analyticalDB.useConnection {
            val statement = it.prepareStatement(
                """
                    select COUNT(0), b.exp_progress=100 as finished, c.course_title
                    from bs_course_stu_progress as b
                    inner join bs_ins_course c on c.course_id = b.course_id
                    group by finished, c.course_title
                    order by course_title, finished;
                """.trimIndent()
            )
            val resultSet = statement.executeQuery()
            val res = mutableListOf<List<Any>>()
            while (resultSet.next()) {
                res.add(listOf(resultSet.getInt(1), resultSet.getBoolean(2), resultSet.getString(3)))
            }
            return res
        }
    }

    /**
     * 获取不同日期下，不同小组（班级）的容器开启数量
     */
    fun getContainerOpenCountByDateAndGroup(): List<List<Any>> {
        analyticalDB.useConnection {
            val statement = it.prepareStatement(
                """
                    select LEFT(t.group_name, 4) as group_name,
                           DATE_FORMAT(t.starttime, '%Y-%m-%d')  as date,
                           COUNT(*)
                    from cc_ins_container t
                    where t.group_name != '无' and t.group_name != '考试'
                    group by date,group_name;
                """.trimIndent()
            )
            val resultSet = statement.executeQuery()
            val res = mutableListOf<List<Any>>()
            while (resultSet.next()) {
                res.add(listOf(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3)))
            }
            return res
        }
    }

    /**
     * 获取最近开启的number个容器信息
     */
    fun getContainerOpenRecent(number: Int): List<RecentContainerInfoDTO> {
        analyticalDB.useConnection {
            val statement = it.prepareStatement(
                """
                    select t.id,
                           s.student_name  as '学生姓名',
                           t.user_name     as '学号',
                           CONCAT(t.cpu,'核心-',ROUND(t.memory / 1024),'G内存-',t.disk,'G磁盘空间') as '节点信息',
                           e.exp_title     as '任务名称',
                           t.starttime
                    from cc_ins_container t
                             inner join bs_ins_stu_group_member s on s.account = t.user_name
                             left join bs_tem_exp_version_record e on t.exp_id = e.exp_id
                             left join bs_ins_course_record cr on t.record_id = cr.record_id
                             left join bs_ins_course ic on cr.course_id = ic.course_id
                    order by t.starttime desc
                    limit ?;
                """.trimIndent()
            )
            statement.setInt(1, number)
            val resultSet = statement.executeQuery()
            val res = mutableListOf<RecentContainerInfoDTO>()
            while (resultSet.next()) {
                res.add(
                    RecentContainerInfoDTO(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        sdf.format(resultSet.getTimestamp(6))
                    )
                )
            }
            return res
        }
    }

    /**
     * 获取不同课程不同小组的学习人数和平均学习时间
     */
    fun getGroupCourseMaxAndAvgStudyTime(): List<List<Any>> {
        analyticalDB.useConnection {
            val statement = it.prepareStatement(
                """
                    select
                        CONCAT(course_name,'(',LEFT(group_name, 4),')'),
                        AVG(teach_hour),
                        COUNT(distinct u.user_id)
                    from bs_home_record a
                    left join sys_user u on u.user_name = a.create_by
                    left join sys_user_role sur on u.user_id = sur.user_id
                    left join sys_role sr on sur.role_id = sr.role_id
                    where sr.post_code != 'teacher' and group_name != '测试群组1'
                    group by group_name ,course_name;
                """.trimIndent()
            )
            val resultSet = statement.executeQuery()
            val res = mutableListOf<List<Any>>()
            while (resultSet.next()) {
                res.add(
                    listOf(
                        resultSet.getString(1),
                        resultSet.getDouble(2),
                        resultSet.getDouble(3)
                    )
                )
            }
            return res
        }
    }

}