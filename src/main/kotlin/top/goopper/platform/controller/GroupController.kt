package top.goopper.platform.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.GroupService

@RestController
class GroupController(private val groupService: GroupService) {

    @GetMapping("/group")
    fun group(): ResponseEntity<Response> {
        val groups = groupService.getByTeacherId()
        return ResponseEntity.ok(Response.success(groups))
    }

}