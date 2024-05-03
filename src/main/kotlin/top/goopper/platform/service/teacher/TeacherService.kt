package top.goopper.platform.service.teacher

import org.springframework.stereotype.Service
import top.goopper.platform.dao.teacher.TeacherDAO
import top.goopper.platform.dto.teacher.TeacherListDTO

@Service
class TeacherService(
    private val teacherDAO: TeacherDAO
) {

    fun getTeacherList(): List<TeacherListDTO> {
        val teachers = teacherDAO.getTeacherList()
        return teachers
    }

}