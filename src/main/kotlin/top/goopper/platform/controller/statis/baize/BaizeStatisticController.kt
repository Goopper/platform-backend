package top.goopper.platform.controller.statis.baize

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.statistic.BaizeStatisticService

@RestController
@RequestMapping("/statistic/baize")
class BaizeStatisticController(
    private val baizeStatisticService: BaizeStatisticService
) {

    /**
     * 不同课程授课数量
     */
    @GetMapping("/course/teach/count")
    fun getCourseTeachCount(): ResponseEntity<Response> {
        val res = baizeStatisticService.getCourseTeachCount()

        return ResponseEntity.ok(Response.success(res))
    }

    /**
     * 容器开启时长前10的学生
     */
    @GetMapping("/container/open/duration/top")
    fun getContainerOpenDurationTop(): ResponseEntity<Response> {
        val res = baizeStatisticService.getContainerOpenDurationTop(10)

        return ResponseEntity.ok(Response.success(res))
    }

    /**
     * 获取最近开启的30个容器信息
     */
    @GetMapping("/container/open/recent")
    fun getContainerOpenRecent(): ResponseEntity<Response> {
        val res = baizeStatisticService.getContainerOpenRecent(30)

        return ResponseEntity.ok(Response.success(res))
    }

    /**
     * 学生完成课程情况
     */
    @GetMapping("/student/course/finished/status")
    fun getStuCourseFinishedStatus(): ResponseEntity<Response> {
        val res = baizeStatisticService.getStuCourseFinishedStatus()

        return ResponseEntity.ok(Response.success(res))
    }

    /**
     * 不同日期下，不同小组（班级）的容器开启数量
     */
    @GetMapping("/group/container/open/count")
    fun getGroupContainerOpenCount(): ResponseEntity<Response> {
        val res = baizeStatisticService.getGroupContainerOpenCount()

        return ResponseEntity.ok(Response.success(res))
    }

    /**
     * 不同课程不同小组的学习人数和平均学习时间
     */
    @GetMapping("/group/course/study/time")
    fun getGroupCourseMaxAndAvgStudyTime(): ResponseEntity<Response> {
        val res = baizeStatisticService.getGroupCourseMaxAndAvgStudyTime()

        return ResponseEntity.ok(Response.success(res))
    }

}