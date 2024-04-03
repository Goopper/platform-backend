package top.goopper.platform.controller.user

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.oauth.OAuthService
import top.goopper.platform.service.UserService

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val oauthService: OAuthService
) {

    @GetMapping
    fun me(): ResponseEntity<Response> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        return ResponseEntity.ok(Response.success(user))
    }

    /**
     * Get current user's OAuth binding List
     */
    @GetMapping("/binds")
    fun binds(): ResponseEntity<Response> {
        val binds = oauthService.getOAuthBindingList()
        return ResponseEntity.ok(Response.success(binds))
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/{id}")
    fun info(@PathVariable id: Long): ResponseEntity<Response> {
        val user = userService.loadUserById(id)
        return ResponseEntity.ok(Response.success(user))
    }

    @GetMapping("/device")
    fun device(): ResponseEntity<Response> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val device = userService.loadDevice(user.id)
        return ResponseEntity.ok(Response.success(device))
    }

    @PutMapping("/email")
    fun updateEmail(@RequestParam old: String, @RequestParam new: String): ResponseEntity<Response> {
        userService.updateEmail(old, new)
        return ResponseEntity.ok(Response.success("Email updated"))
    }

    @PutMapping("/password")
    fun updatePassword(@RequestParam old: String, @RequestParam new: String): ResponseEntity<Response> {
        userService.updatePassword(old, new)
        return ResponseEntity.ok(Response.success("Password updated, please re-login"))
    }

}