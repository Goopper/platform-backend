package top.goopper.platform.controller.user

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.UserService
import top.goopper.platform.service.oauth.OAuthService

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val oauthService: OAuthService
) {
    // 设置最大文件大小为5MB
    val maxAvatarSize = (5 * 1024 * 1024).toLong()

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
    fun info(@PathVariable id: Int): ResponseEntity<Response> {
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

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PutMapping("/student/password")
    fun updateStudentPassword(@RequestParam password: String, @RequestParam uid: Int): ResponseEntity<Response> {
        userService.updateStudentPassword(password, uid)
        return ResponseEntity.ok(Response.success("Password updated."))
    }

    @PostMapping("/avatar")
    fun uploadAvatar(@RequestParam avatar: MultipartFile): ResponseEntity<Response> {
        if (avatar.size > maxAvatarSize) {
            return ResponseEntity.badRequest().body(Response.error(400, "File size too large (>5MB)"))
        }
        if (!avatar.contentType!!.startsWith("image")) {
            return ResponseEntity.badRequest().body(Response.error(400, "File type not supported"))
        }
        if (avatar.isEmpty) {
            return ResponseEntity.badRequest().body(Response.error(400, "File is empty"))
        }
        val url = userService.uploadAvatar(avatar)
        return ResponseEntity.ok(Response.success(url))
    }

    @PutMapping("/avatar")
    fun updateAvatar(@RequestParam url: String): ResponseEntity<Response> {
        userService.updateAvatar(url)
        return ResponseEntity.ok(Response.success("Avatar updated"))
    }

}