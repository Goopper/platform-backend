package top.goopper.platform.service.statistic

import org.springframework.stereotype.Service
import top.goopper.platform.dao.statistic.baize.BaizeStatisticDAO
import top.goopper.platform.dto.statistic.baize.RecentContainerInfoDTO

@Service
class BaizeStatisticService(
    private val baizeStatisticDAO: BaizeStatisticDAO
) {

    /**
     * 获取课程教学实验记录（每个课程实验次数）
     */
    fun getCourseTeachCount(): List<List<Any>> {
        val res = baizeStatisticDAO.getCourseTeachCountByCourseTitle()
        return res
    }

    /**
     * 获取容器开启时长(小时)前number的学生
     */
    fun getContainerOpenDurationTop(number: Int): List<List<Any>> {
        val res = baizeStatisticDAO.getContainerOpenDurationTop(number)
        return res
    }

    /**
     * 获取学生完成课程的情况（已完成和未完成）
     */
    fun getStuCourseFinishedStatus(): List<List<Any>> {
        val status = baizeStatisticDAO.getStuCourseFinishedStatus()
        val res = status.groupBy { it[2] }.map { group ->
            val counts = group.value.map { it[0] }
            listOf(group.key) + counts
        }
        return res
    }

    /**
     * 获取不同日期下，不同小组（班级）的容器开启数量
     */
    fun getGroupContainerOpenCount(): List<List<Any>> {
        val counts = baizeStatisticDAO.getContainerOpenCountByDateAndGroup()
        // firstLine example: product, 2011班, 1911班, 2111班
        val firstLine = listOf("product") + counts.map { it[0] }.distinct()
        val res = mutableListOf(firstLine)
        // secondLine example: 2021-01-01, 10, 20, 30 or 2021-01-01, '-', 20, '-', 40
        counts.groupBy { it[1] }.forEach { group ->
            val line = mutableListOf(group.key)
            firstLine.subList(1, firstLine.size).forEach { product ->
                val count = group.value.find { it[0] == product }?.get(2) ?: "-"
                line.add(count)
            }
            res.add(line)
        }
        return res
    }

    /**
     * 获取最近开启的number个容器信息
     */
    fun getContainerOpenRecent(number: Int): List<RecentContainerInfoDTO> {
        val res = baizeStatisticDAO.getContainerOpenRecent(number)

        return res
    }

    /**
     * 不同课程不同小组的学习人数和平均学习时间
     */
    fun getGroupCourseMaxAndAvgStudyTime(): List<List<Any>> {
        val res = baizeStatisticDAO.getGroupCourseMaxAndAvgStudyTime()
        return res
    }

}