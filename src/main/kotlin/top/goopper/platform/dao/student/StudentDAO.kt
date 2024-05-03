package top.goopper.platform.dao.student

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.StudentDTO
import top.goopper.platform.dto.course.CourseDTO
import top.goopper.platform.dto.course.progress.StudentLearningProgressDTO
import top.goopper.platform.enum.CourseStatusEnum
import top.goopper.platform.table.Group
import top.goopper.platform.table.User
import top.goopper.platform.table.course.Course
import top.goopper.platform.table.course.CourseType
import top.goopper.platform.table.student.StudentCourse

@Repository
class StudentDAO(private val database: Database) {

    /**
     * Query all student's id in groups
     */
    fun getIdByGroups(groupIds: List<Int>): MutableList<Int> {
        val studentIds = database.from(User)
            .innerJoin(Group, User.groupId eq Group.id)
            .select(User.id)
            .where { Group.id inList groupIds }
            .map { it[User.id]!! }
        return studentIds.toMutableList()
    }

    fun getAllLearningProgress(uid: Int, typeId: Int?, name: String): List<StudentLearningProgressDTO> {
        val result = database.from(StudentCourse)
            .innerJoin(User, StudentCourse.studentId eq User.id)
            .innerJoin(Course, StudentCourse.courseId eq Course.id)
            .innerJoin(CourseType, Course.typeId eq CourseType.id)
            .select()
            .where {
                var condition = (StudentCourse.studentId eq uid) and
                        (Course.statusId eq CourseStatusEnum.USING.id) and
                        (Course.name like "%$name%")
                if (typeId != null) {
                    condition = condition and (Course.typeId eq typeId)
                }
                condition
            }
            .orderBy(StudentCourse.modifyTime.desc())
            .map {
                StudentLearningProgressDTO(
                    id = uid,
                    course = CourseDTO(
                        id = it[Course.id]!!,
                        name = it[Course.name]!!,
                        desc = it[Course.desc]!!,
                        cover = it[Course.cover]!!,
                        type = it[CourseType.name]!!,
                        // student's course status is always using
                        status = CourseStatusEnum.USING.desc
                    ),
                    finishedTask = it[StudentCourse.finishedTask]!!,
                    totalTask = it[Course.totalTask]!!,
                )
            }

        return result
    }

    /**
     * Get student's latest learning progress.
     * Order by modify time desc top 1. (latest)
     * @param uid student id
     */
    fun getLatestLearningProgress(uid: Int): StudentLearningProgressDTO {
        val result = database.from(StudentCourse)
            .innerJoin(User, StudentCourse.studentId eq User.id)
            .innerJoin(Course, StudentCourse.courseId eq Course.id)
            .innerJoin(CourseType, Course.typeId eq CourseType.id)
            .select()
            .where {
                (StudentCourse.studentId eq uid) and
                        (Course.statusId eq CourseStatusEnum.USING.id)
            }
            .orderBy(StudentCourse.modifyTime.desc())
            .limit(1)
            .map {
                StudentLearningProgressDTO(
                    id = uid,
                    course = CourseDTO(
                        id = it[Course.id]!!,
                        name = it[Course.name]!!,
                        desc = it[Course.desc]!!,
                        cover = it[Course.cover]!!,
                        type = it[CourseType.name]!!,
                        // student's course status is always using
                        status = CourseStatusEnum.USING.desc
                    ),
                    finishedTask = it[StudentCourse.finishedTask]!!,
                    totalTask = it[Course.totalTask]!!,
                )
            }
            .firstOrNull() ?: throw Exception("No course found")

        return result
    }

    /**
     * Get available course for student.
     * Available course is the course that `student has not joined` and `already published`.
     * @param uid student id
     */
    fun getAvailableCourse(uid: Int, typeId: Int?, name: String): List<CourseDTO> {
        val result = database.from(Course)
            .innerJoin(CourseType, Course.typeId eq CourseType.id)
            .leftJoin(
                StudentCourse,
                (Course.id eq StudentCourse.courseId) and (StudentCourse.studentId eq uid)
            )
            .select()
            .where {
                var condition = (Course.statusId eq CourseStatusEnum.USING.id) and
                        (StudentCourse.id.isNull()) and
                        (Course.name like "%$name%")
                if (typeId != null) {
                    condition = condition and (Course.typeId eq typeId)
                }
                condition
            }
            .map {
                CourseDTO(
                    id = it[Course.id]!!,
                    name = it[Course.name]!!,
                    cover = it[Course.cover]!!,
                    type = it[CourseType.name]!!,
                    desc = it[Course.desc]!!,
                    // student's course status is always using
                    status = CourseStatusEnum.USING.desc
                )
            }
        return result
    }

    fun getStudentsLearningProgress(courseId: Int, groupId: Int, order: OrderByExpression): List<StudentDTO> {
        val result = database.from(User)
            .innerJoin(StudentCourse, User.id eq StudentCourse.studentId)
            .innerJoin(Course, StudentCourse.courseId eq Course.id)
            .select()
            .where {
                (StudentCourse.courseId eq courseId) and
                        (User.groupId eq groupId)
            }
            .orderBy(order)
            .map {
                StudentDTO(
                    id = it[User.id]!!,
                    number = it[User.number]!!,
                    name = it[User.name]!!,
                    avatar = it[User.avatar]!!,
                    finishedTask = it[StudentCourse.finishedTask]!!,
                    totalTask = it[Course.totalTask]!!,
                    sectionName = null,
                    taskName = null,
                    lastUpdate = null
                )
            }
        return result
    }

    fun getIdByCourse(courseId: Int): List<Int> {
        val studentIds = database.from(StudentCourse)
            .select(StudentCourse.studentId)
            .where { StudentCourse.courseId eq courseId }
            .map { it[StudentCourse.studentId]!! }
        return studentIds
    }

}