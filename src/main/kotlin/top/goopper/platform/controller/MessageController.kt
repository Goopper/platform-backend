package top.goopper.platform.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.pojo.Response

@RestController
@RequestMapping("/message")
class MessageController {

    @GetMapping
    fun messages(): ResponseEntity<Response> {
        return ResponseEntity.ok(Response.success(""))
    }

}