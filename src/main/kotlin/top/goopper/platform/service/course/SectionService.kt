package top.goopper.platform.service.course

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.goopper.platform.dao.section.SectionDAO
import top.goopper.platform.dao.task.TaskDAO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.course.create.CreateSectionDTO
import top.goopper.platform.dto.course.detail.SectionDetailDTO
import top.goopper.platform.enum.RoleEnum

@Service
class SectionService(
    private val sectionDAO: SectionDAO,
    private val taskDAO: TaskDAO
) {

    fun createNewSection(section: CreateSectionDTO) {
        sectionDAO.createSection(section)
    }

    fun getCreationInfo(sectionId: Int): CreateSectionDTO {
        val creationInfo = sectionDAO.loadSectionCreationInfo(sectionId)
        return creationInfo
    }

    fun updateSection(section: CreateSectionDTO) {
        sectionDAO.modifySection(section)
    }

    /**
     * Delete section and all tasks in it
     * Course total_task will be auto decremented by trigger in database `section_delete_trigger`
     */
    fun deleteSection(sectionId: Int) {
        sectionDAO.removeSection(sectionId)
    }

    /**
     * Get section detail with tasks
     * If current user is student, only return tasks and task status of current user
     * If current user is teacher, return all tasks
     */
    fun getSectionDetail(sectionId: Int): SectionDetailDTO {
        val section = sectionDAO.loadSectionDetail(sectionId)
        val auth = SecurityContextHolder.getContext().authentication
        val role = auth.authorities.first().authority
        // check roles
        if (role == "ROLE_${RoleEnum.STUDENT.name}") {
            val uid = (auth.principal as UserDTO).id
            val tasks = taskDAO.studentLoadTasksBySectionId(sectionId, uid)
            section.tasks = tasks
        } else {
            val tasks = taskDAO.teacherLoadTasksBySectionId(sectionId)
            section.tasks = tasks
        }
        return section
    }

}