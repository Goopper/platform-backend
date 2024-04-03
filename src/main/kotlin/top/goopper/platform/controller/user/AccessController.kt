package top.goopper.platform.controller.user

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.config.SecurityConfig.Companion.AUTHORIZATION_HEADER
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.UserService

@RestController
class AccessController(
    private val userService: UserService,
) {

    /**
     * 用户登陆
     * @param number 用户学号/工号
     * @param password 用户密码
     * @param request 请求
     * @return 登陆成功返回jwt
     * @throws Exception 登陆失败抛出异常
     */
    @PostMapping("/login")
    fun login(
        @RequestParam number: Long,
        @RequestParam password: String,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        val userAgent = request.getHeader("User-Agent")
        val jwt = userService.authenticate(number, password, userAgent)
        return ResponseEntity.ok(Response.success(jwt))
    }

    /**
     * 用户登出
     * @return 登出成功返回成功信息
     */
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Response> {
        val token = request.getHeader(AUTHORIZATION_HEADER)
        userService.logout(token)
        return ResponseEntity.ok(Response.success())
    }
}