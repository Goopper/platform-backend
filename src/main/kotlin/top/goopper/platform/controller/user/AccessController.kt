package top.goopper.platform.controller.user

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import top.goopper.platform.config.SecurityConfig.Companion.AUTHORIZATION_HEADER
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.UserService

@RestController
class AccessController(
    private val userService: UserService,
) {

    private val logger = LoggerFactory.getLogger(AccessController::class.java)

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
        @RequestParam number: Int,
        @RequestParam password: String,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        var forwardIps = "null"
        try {
            forwardIps = request.getHeader("X-Forwarded-For")
        } catch (e: NullPointerException) {
            logger.error("Login request error: Forwarded-For is null, userNumber: $number")
        }
        val userAgent = request.getHeader("User-Agent")
        val jwt = userService.authenticate(number, password, userAgent, forwardIps)
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

    /**
     * 登出指定设备
     *
     * @param tokenId 设备id（jwt的hash）
     */
    @DeleteMapping("/logout")
    fun logout(tokenId: Int): ResponseEntity<Response> {
        userService.logout(tokenId)
        return ResponseEntity.ok(Response.success())
    }
}