package top.goopper.platform.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import top.goopper.platform.pojo.Response

@RestController
class HealthController {
    @GetMapping("/health")
    fun health(): ResponseEntity<Response> {
        return ResponseEntity.ok(Response.success("OK"))
    }
}