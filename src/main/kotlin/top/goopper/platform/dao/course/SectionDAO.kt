package top.goopper.platform.dao.course

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dto.course.SectionDTO
import top.goopper.platform.dto.course.TaskDTO
import top.goopper.platform.dto.course.create.CreateSectionDTO
import top.goopper.platform.dto.course.detail.SectionDetailDTO
import top.goopper.platform.table.Section
import top.goopper.platform.table.Task

@Component
class SectionDAO(private val database: Database) {

    fun createSection(createSectionDTO: CreateSectionDTO) {
        try {
            database.insert(Section) {
                set(it.courseId, createSectionDTO.courseId)
                set(it.name, createSectionDTO.name)
                set(it.desc, createSectionDTO.desc)
            }
        } catch (e: DataIntegrityViolationException) {
            throw Exception("Create section failed, course does not exist")
        }
    }

    fun loadSectionCreationInfo(sectionId: Long): CreateSectionDTO {
        val creationInfo = database.from(Section)
            .select()
            .where { Section.id eq sectionId }
            .map { row ->
                CreateSectionDTO(
                    id = row[Section.id]!!,
                    courseId = row[Section.courseId]!!,
                    name = row[Section.name]!!,
                    desc = row[Section.desc]!!
                )
            }
            .firstOrNull()
            ?: throw Exception("Section not found")
        return creationInfo
    }

    @Transactional(rollbackFor = [Exception::class])
    fun modifySection(section: CreateSectionDTO) {
        database.update(Section) {
            set(it.courseId, section.courseId)
            set(it.name, section.name)
            set(it.desc, section.desc)
            where { it.id eq section.id!! }
        }
    }

    fun removeSection(sectionId: Long) {
        database.delete(Section) {
            it.id eq sectionId
        }
    }

    fun loadSectionDetail(sectionId: Long): SectionDetailDTO {
        val section = database.from(Section)
            .select()
            .where { Section.id eq sectionId }
            .map { row ->
                SectionDetailDTO(
                    id = row[Section.id]!!,
                    name = row[Section.name]!!,
                    desc = row[Section.desc]!!,
                    tasks = emptyList()
                )
            }
            .firstOrNull()
            ?: throw Exception("Section not found")
        return section
    }

    /**
     * Load section list of a course
     * @param courseId course id
     */
    fun loadCourseSectionList(courseId: Long): List<SectionDTO> {
        val query = database.from(Section)
            .leftJoin(Task, Section.id eq Task.sectionId)
            .select()
            .where { Section.courseId eq courseId }
        val sections = query
            .map {
                val taskId = it[Task.id]
                val sectionDTO: SectionDTO
                if (taskId != null) {
                    sectionDTO = SectionDTO(
                        id = it[Section.id]!!,
                        name = it[Section.name]!!,
                        tasks = listOf(
                            TaskDTO(
                                id = taskId,
                                name = it[Task.name]!!,
                                status = false,
                                score = 0
                            )
                        )
                    )
                } else {
                    sectionDTO = SectionDTO(
                        id = it[Section.id]!!,
                        name = it[Section.name]!!,
                        tasks = emptyList()
                    )
                }
                sectionDTO
            }
            .groupBy { it.id }
            .mapValues { (_, value) ->
                val section = value.first()
                section.tasks = value.flatMap { it.tasks }.sortedBy { it.id }
                section
            }
            .values.toList()
        return sections
    }

}