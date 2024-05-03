package top.goopper.platform.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.goopper.platform.dao.statistic.StatisticDAO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.statistic.StudentPerformancePageDTO
import top.goopper.platform.dto.statistic.StudentPerformanceQueryDTO

@Service
class StatisticService(
    private val statisticDAO: StatisticDAO
) {
    private val pageSize: Int = 10

    fun getStudentPerformance(dto: StudentPerformanceQueryDTO): StudentPerformancePageDTO {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        dto.teacherId = user.id
        val performance = statisticDAO.getStudentPerformance(dto, pageSize)
        return performance
    }

}