package top.goopper.platform.dao.course

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import top.goopper.platform.dto.AttachmentDTO
import top.goopper.platform.dto.course.CourseDTO
import top.goopper.platform.dto.course.create.CreateCourseDTO
import top.goopper.platform.dto.course.detail.CourseDetailDTO
import top.goopper.platform.table.*
import java.time.LocalDateTime

@Component
class CourseDAO(
    private val database: Database,
) {

    fun createCourse(createCourseDTO: CreateCourseDTO): Long {
        val courseId = database.insertAndGenerateKey(Course) {
            set(it.name, createCourseDTO.name)
            set(it.teacherId, createCourseDTO.teacherId)
            set(it.typeId, createCourseDTO.typeId)
            set(it.desc, createCourseDTO.desc)
            set(it.cover, createCourseDTO.cover)
            set(it.statusId, 1)
            set(it.totalTask, 0)
        } as Long
        return courseId
    }

    fun loadCourseCreationInfo(courseId: Long): CreateCourseDTO {
        val creation = database.from(Course)
            .select()
            .where(Course.id eq courseId)
            .map {
                CreateCourseDTO(
                    id = it[Course.id],
                    name = it[Course.name]!!,
                    teacherId = it[Course.teacherId]!!,
                    typeId = it[Course.typeId]!!,
                    desc = it[Course.desc]!!,
                    cover = it[Course.cover]!!,
                    attachments = emptyList()
                )
            }.firstOrNull() ?: throw Exception("Course not found")
        return creation
    }

    /**
     * Modify course info, will check the attachment conflict and append new attachments
     * conflict attachment: attachment not deleted by user
     */
    fun modifyCourse(course: CreateCourseDTO, newAttachments: List<AttachmentDTO>) {
        database.update(Course) {
            set(it.name, course.name)
            set(it.teacherId, course.teacherId)
            set(it.typeId, course.typeId)
            set(it.desc, course.desc)
            set(it.cover, course.cover)
            set(it.modifyTime, LocalDateTime.now())
            where {
                it.id eq course.id!!
            }
        }
    }

    fun deleteCourse(courseId: Long) {
        database.delete(Course) {
            it.id eq courseId
        }
    }

    fun loadCourseDetail(courseId: Long): CourseDetailDTO {
        val course = database.from(Course)
            .innerJoin(CourseType, CourseType.id eq Course.typeId)
            .innerJoin(User, User.id eq Course.teacherId)
            .select()
            .where(Course.id eq courseId)
            .map {
                CourseDetailDTO(
                    id = it[Course.id]!!,
                    name = it[Course.name]!!,
                    teacher = it[User.name]!!,
                    type = it[CourseType.name]!!,
                    desc = it[Course.desc]!!,
                    cover = it[Course.cover]!!,
                    totalTask = it[Course.totalTask]!!,
                    attachments = emptyList()
                )
            }.firstOrNull() ?: throw Exception("Course not found")
        return course
    }

    fun loadTeacherCourseList(id: Long): List<CourseDTO> {
        val courseList = database.from(Course)
            .innerJoin(CourseType, CourseType.id eq Course.typeId)
            .select()
            .where(Course.teacherId eq id)
            .map {
                CourseDTO(
                    id = it[Course.id]!!,
                    name = it[Course.name]!!,
                    type = it[CourseType.name]!!,
                    desc = it[Course.desc]!!,
                    cover = it[Course.cover]!!,
                )
            }
        return courseList
    }

}