package top.goopper.platform.service

import org.ktorm.dsl.asc
import org.ktorm.dsl.desc
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.goopper.platform.dao.section.SectionDAO
import top.goopper.platform.dao.student.StudentDAO
import top.goopper.platform.dto.StudentDTO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.course.CourseDTO
import top.goopper.platform.dto.course.SectionDTO
import top.goopper.platform.dto.course.progress.StudentLearningProgressDTO
import top.goopper.platform.enum.StudentOrderEnum
import top.goopper.platform.table.User
import top.goopper.platform.table.student.StudentCourse
import top.goopper.platform.utils.RedisUtils
import java.time.Year

@Service
class StudentService(
    private val studentDAO: StudentDAO,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redisUtils: RedisUtils,
    private val sectionDAO: SectionDAO
) {
    /**
     * Get current learning course list for student
     */
    fun getCurrentLearningCourseList(typeId: Int?, name: String): List<StudentLearningProgressDTO> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val result = studentDAO.getAllLearningProgress(user.id, typeId, name)
        redisTemplate.execute {
            for (dto in result) {
                fillProgressDetail2ProgressDTO(user.id, dto, it)
            }
        }
        return result
    }

    /**
     * Get available course list for student
     */
    fun getAvailableCourse(typeId: Int?, name: String): List<CourseDTO> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val courses = studentDAO.getAvailableCourse(user.id, typeId, name)
        return courses
    }

    /**
     * Get student's latest learning course
     */
    fun getLatestLearningCourse(uid: Int): StudentLearningProgressDTO {
        val progress = studentDAO.getLatestLearningProgress(uid)
        redisTemplate.execute {
            fillProgressDetail2ProgressDTO(uid, progress, it)
        }
        return progress
    }

    fun getCourseLearningProgressDetail(studentId: Int, courseId: Int): List<SectionDTO> {
        val result = sectionDAO.loadStudentCourseSectionList(studentId, courseId)
        return result
    }

    /**
     * load all student learning info by group and course
     */
    fun getStudentsLearningProgress(courseId: Int, groupId: Int, orderId: Int): List<StudentDTO> {
        val order = when (orderId) {
            StudentOrderEnum.ASC_NUMBER.value -> User.number.asc()
            StudentOrderEnum.DESC_PROGRESS.value -> StudentCourse.finishedTask.desc()
            else -> throw IllegalArgumentException("Invalid order type")
        }
        val result = studentDAO.getStudentsLearningProgress(courseId, groupId, order)
        redisTemplate.execute {
            for (dto in result) {
                val key = redisUtils.buildLatestLearnedKey(dto.id, courseId)
                val status = it.hashCommands().hGetAll(key).orEmpty()
                if (status.isEmpty()) {
                    continue
                }
                status.forEach { (k, v) ->
                    val targetKey = String(k)
                    val value = String(v)
                    when (targetKey) {
                        redisUtils.LATEST_LEARNED_SECTION_STR -> dto.sectionName = value
                        redisUtils.LATEST_LEARNED_TASK_STR -> dto.taskName = value
                        redisUtils.LATEST_LEARNED_DATE_STR -> dto.lastUpdate = value
                    }
                }
            }
        }
        return result
    }

    /**
     * Load progress data from redis and fill into dto
     */
    private fun fillProgressDetail2ProgressDTO(uid: Int, dto: StudentLearningProgressDTO, connection: RedisConnection) {
        val key = redisUtils.buildLatestLearnedKey(uid, dto.course.id)
        val status = connection.hashCommands().hGetAll(key).orEmpty()

        if (status.isNotEmpty()) {
            val iterator = status.iterator()
            dto.lastSectionName = String(iterator.next().value)
            dto.lastTaskName = String(iterator.next().value)
            dto.lastLearningDate = String(iterator.next().value)
        }
    }

    /**
     * Get student statistic years
     */
    fun getStudentStatisticYears(): List<String> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val result = emptyList<String>().toMutableList()
        redisTemplate.execute {
            val key = redisUtils.buildLearnedYearsListKey(user.id)
            it.setCommands().sMembers(key).orEmpty().forEach { year ->
                result.add(String(year))
            }
        }
        return result
    }

    fun getStudentFullYearStatistic(year: Int): Any?{
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val key = redisUtils.buildYearLearnedKey(user.id, year)
        val result = mutableListOf<Int>()
        redisTemplate.execute {
            val bitmap = it.stringCommands().get(key)
            if (bitmap != null) {
                for (byte in bitmap) {
                    for (i in 7 downTo 0) {
                        val bit = (byte.toInt() shr i) and 1
                        result.add(bit)
                    }
                }
            }
        }
        val yearLength = Year.of(year).length()
        return result.take(yearLength)
    }
}