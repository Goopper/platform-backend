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
     * 不同时间授课数量
     */
    @GetMapping("/course/teach/count/start-time")
    fun getCourseTeachCountByStartTime(): ResponseEntity<Response> {
        val res = baizeStatisticService.getCourseTeachCountByStartTime()

        return ResponseEntity.ok(Response.success(res))
    }

    /**
     * 容器开启时长前10的学生
     */
    @GetMapping("/container/open/duration/top-ten")
    fun getContainerOpenDurationTopTen(): ResponseEntity<Response> {
        val res = baizeStatisticService.getContainerOpenDurationTopTen()

        return ResponseEntity.ok(Response.success(res))
    }

}