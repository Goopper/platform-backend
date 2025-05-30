package top.goopper.platform.service.course

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dao.AttachmentDAO
import top.goopper.platform.dao.course.CourseAttachmentDAO
import top.goopper.platform.dao.course.CourseDAO
import top.goopper.platform.dao.section.SectionDAO
import top.goopper.platform.dao.student.StudentDAO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.course.CourseDTO
import top.goopper.platform.dto.course.CourseStatusDTO
import top.goopper.platform.dto.course.CourseTypeDTO
import top.goopper.platform.dto.course.SectionDTO
import top.goopper.platform.dto.course.create.CreateCourseDTO
import top.goopper.platform.dto.course.detail.CourseDetailDTO
import top.goopper.platform.enum.CourseStatusEnum

@Service
class CourseService(
    private val courseDAO: CourseDAO,
    private val sectionDAO: SectionDAO,
    private val attachmentDAO: AttachmentDAO,
    private val courseAttachmentDAO: CourseAttachmentDAO,
    private val studentDAO: StudentDAO
) {

    /**
     * Create new course, will create attachment and fill the id
     * @param course course info
     */
    @Transactional(rollbackFor = [Exception::class])
    fun createNewCourse(course: CreateCourseDTO): Int {
        // create attachment and fill the id
        attachmentDAO.batchCreateAttachment(course.attachments)
        // create course
        try {
            val courseId = courseDAO.createCourse(course)
            if (course.attachments.isEmpty()) return courseId
            // create relation between course and attachment
            courseAttachmentDAO.batchInsertCourseAttachment(courseId, course.attachments.map { it.id!! })
            return courseId
        } catch (e: DataIntegrityViolationException) {
            e.printStackTrace()
            throw Exception("Course type or teacher not exists")
        }
    }

    fun getCreationInfo(courseId: Int): CreateCourseDTO {
        val info = courseDAO.loadCourseCreationInfo(courseId)
        info.attachments = attachmentDAO.loadCourseAttachments(courseId)
        return info
    }

    /**
     * Update course info, will check the attachment conflict and append new attachments
     * @param course course info
     * conflict attachment: attachment not deleted by user
     */
    @Transactional(rollbackFor = [Exception::class])
    fun updateCourse(course: CreateCourseDTO) {
        // create new attachment and fill the id
        val newAttachments = course.attachments.filter {
            it.id == null
        }
        // create attachment
        attachmentDAO.batchCreateAttachment(newAttachments)
        // modify course
        courseDAO.modifyCourse(course, newAttachments)
        // add relation between course and attachment
        if (newAttachments.isEmpty()) return
        courseAttachmentDAO.batchInsertCourseAttachment(course.id!!, newAttachments.map { it.id!! })
    }

    fun deleteCourse(courseId: Int) {
        courseDAO.deleteCourse(courseId)
    }

    fun getCourseDetail(courseId: Int): CourseDetailDTO {
        val course = courseDAO.loadCourseDetail(courseId)
        course.attachments = attachmentDAO.loadCourseAttachments(courseId)
        return course
    }

    fun getCourseStructure(courseId: Int): List<SectionDTO> {
        val sections = sectionDAO.loadCourseSectionList(courseId)
        return sections
    }

    fun teacherGetCourseList(statusId: Int?, name: String): List<CourseDTO> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val courseList = courseDAO.loadTeacherCourseList(user.id, statusId, name)
        return courseList
    }

    /**
     * Teacher publish course, set course status to using
     */
    fun publishCourse(courseId: Int) {
        courseDAO.publishCourse(courseId)
    }

    /**
     * Teacher apply course with students in groups
     */
    fun applyCourseWithStudentInGroups(courseId: Int, groupIds: List<Int>) {
        // course status must be using
        val status = courseDAO.loadStatusById(courseId)
        if (status != CourseStatusEnum.USING.id) {
            throw Exception("Course is not published")
        }
        // make sure student not in course
        val existsStudentIds = studentDAO.getIdByCourse(courseId)
        val studentIds = studentDAO.getIdByGroups(groupIds)
        studentIds.removeAll(existsStudentIds)
        if (studentIds.isNotEmpty()) {
            courseDAO.applyCourseWithStudents(courseId, studentIds)
        }
    }

    /**
     * Student manual select course
     */
    fun manualSelectCourse(courseId: Int) {
        // course status must be using
        val status = courseDAO.loadStatusById(courseId)
        if (status != CourseStatusEnum.USING.id) {
            throw Exception("Course is not published")
        }
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        courseDAO.applyCourseWithStudents(courseId, listOf(user.id))
    }

    fun disableCourse(courseId: Int) {
        val statusId = courseDAO.loadStatusById(courseId)
        if (statusId != CourseStatusEnum.USING.id) {
            throw Exception("Course is not published")
        }
        courseDAO.disableCourse(courseId)
    }

    fun enableCourse(courseId: Int) {
        val statusId = courseDAO.loadStatusById(courseId)
        if (statusId != CourseStatusEnum.DEACTIVATED.id) {
            throw Exception("Course is not disabled")
        }
        courseDAO.enableCourse(courseId)
    }

    fun getCourseTypes(): List<CourseTypeDTO> {
        val result = courseDAO.getCourseTypes()
        return result
    }

    /**
     * 创建课程类型
     *
     * @param typeName 课程类型名称
     * @throws Exception 课程类型已存在
     */
    fun createCourseType(typeName: String) {
        val exists = courseDAO.checkCourseTypeExists(typeName)
        if (exists) {
            throw Exception("Course type exists")
        }
        courseDAO.createCourseType(typeName)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun copyCourse(courseId: Int) {
        val course = courseDAO.loadCourseCopyInfo(courseId)
        if (course.statusId == CourseStatusEnum.USING.id) {
            throw Exception("Course is using")
        }
        course.name += " - 副本"
        val attachments = attachmentDAO.loadCourseAttachments(courseId)
        attachmentDAO.batchCreateAttachment(attachments)
        course.attachments = attachments
        // create course
        val newCourseId = courseDAO.createCourse(course)
        if (course.attachments.isEmpty()) return
        // create relation between course and attachment
        courseAttachmentDAO.batchInsertCourseAttachment(newCourseId, course.attachments.map { it.id!! })
    }

    fun deleteCourseType(typeId: Int) {
        courseDAO.deleteCourseType(typeId)
    }

    fun deleteCourseAttachment(courseId: Int, attachmentId: Int) {
        courseAttachmentDAO.deleteCourseAttachment(courseId, attachmentId)
    }

    fun getCourseStatus(): List<CourseStatusDTO> {
        val status = courseDAO.getCourseStatus()
        return status
    }

}