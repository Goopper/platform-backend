package top.goopper.platform.service.statistic

import org.springframework.stereotype.Service
import top.goopper.platform.dao.statistic.baize.BaizeStatisticDAO

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
     * 获取课程教学实验记录（不同时间段次数）
     */
    fun getCourseTeachCountByStartTime(): List<List<Any>> {
        val res = baizeStatisticDAO.getCourseTeachCountByDate()
        return res
    }

    /**
     * 获取容器开启时长(小时)前十的学生
     */
    fun getContainerOpenDurationTopTen(): List<List<Any>> {
        val res = baizeStatisticDAO.getContainerOpenDurationTop10()
        return res
    }

}