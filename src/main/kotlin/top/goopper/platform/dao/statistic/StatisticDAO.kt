package top.goopper.platform.dao.statistic

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.statistic.StudentPerformanceDTO
import top.goopper.platform.dto.statistic.StudentPerformanceQueryDTO
import top.goopper.platform.table.Group
import top.goopper.platform.table.User
import top.goopper.platform.table.course.Course
import top.goopper.platform.table.student.StudentCourse

@Repository
class StatisticDAO(private val database: Database) {
    fun getStudentPerformance(dto: StudentPerformanceQueryDTO): List<StudentPerformanceDTO> {
        val result = database.from(User)
            .innerJoin(Group, User.groupId eq Group.id)
            .innerJoin(StudentCourse, User.id eq StudentCourse.studentId)
            .innerJoin(Course, StudentCourse.courseId eq Course.id)
            .select(
                User.id,
                User.name,
                Group.name,
                Course.name,
                StudentCourse.finishedTask,
                Course.totalTask
            )
            .where {
                var condition = Group.teacherId eq dto.teacherId!!
                if (dto.studentName != null) {
                    condition = condition and (User.name like "%${dto.studentName}%")
                }
                if (dto.groupId != null) {
                    condition = condition and (Group.id eq dto.groupId)
                }
                if (dto.courseTypeId != null) {
                    condition = condition and (Course.typeId eq dto.courseTypeId)
                }
                condition
            }
            .map {
                StudentPerformanceDTO(
                    it[User.id]!!,
                    it[User.name]!!,
                    it[Group.name]!!,
                    it[Course.name]!!,
                    it[StudentCourse.finishedTask]!!,
                    it[Course.totalTask]!!
                )
            }
        return result
    }
}