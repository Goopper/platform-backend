package top.goopper.platform.dao.statistic.baize

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import top.goopper.platform.table.baize.course.BsInsCourse
import top.goopper.platform.table.baize.course.BsInsCourseMultiRecord
import top.goopper.platform.table.baize.course.BsInsCourseRecord

@Repository
class BaizeStatisticDAO(
    @Qualifier("analyticalDB") private val analyticalDB: Database
) {

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
     * 获取不同时间段课程教学实验记录
     */
    fun getCourseTeachCountByDate(): List<List<Any>> {
        analyticalDB.useConnection {
            val statement = it.prepareStatement(
                """
                    select
                        DATE_FORMAT(t.start_time,'%Y-%m-%d'),
                        COUNT(*)
                    from bs_ins_course_record t
                    inner join bs_ins_course c on t.course_id=c.course_id
                    inner join bs_ins_course_multi_record e on t.course_id=e.resource_id
                    where t.record_type=?
                    group by DATE_FORMAT(t.start_time,'%Y-%m-%d')
                    order by t.start_time;
                """.trimIndent()
            )
            statement.setString(1, "course")
            val resultSet = statement.executeQuery()
            val res = mutableListOf<List<Any>>()
            while (resultSet.next()) {
                res.add(listOf(resultSet.getString(1), resultSet.getInt(2)))
            }
            return res
        }
    }

    /**
     * 获取容器开启时长前10的学生
     */
    fun getContainerOpenDurationTop10(): List<List<Any>> {
        analyticalDB.useConnection {
            val statement = it.prepareStatement(
                """
                    select t.user_name,
                           SUM((t.endtime - t.completetime) / 60 / 60) as totaltime
                    from cc_ins_container t
                    where t.user_id != t.teach_by_id
                    group by user_id order by totaltime desc LIMIT 10;
                """.trimIndent()
            )
            val resultSet = statement.executeQuery()
            val res = mutableListOf<List<Any>>()
            while (resultSet.next()) {
                res.add(listOf(resultSet.getString(1), resultSet.getInt(2)))
            }
            return res
        }
    }

}