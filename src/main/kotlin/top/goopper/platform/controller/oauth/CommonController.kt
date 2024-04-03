package top.goopper.platform.controller.oauth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.oauth.OAuthService

@RestController
@RequestMapping("/oauth")
class CommonController(private val oauthService: OAuthService) {
    /**
     * GitHub OAuth client login
     * @param oauthId oauth id
     * @param request http request, provide user agent
     */
    @PostMapping("/login/{providerName}")
    fun login(
        @RequestParam oauthId: String,
        @PathVariable providerName: String,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        val jwt = oauthService.authenticate(
            oauthId,
            providerName,
            request.getHeader("User-Agent")
        )
        return ResponseEntity.ok(Response.success(jwt))
    }

    /**
     * GitHub OAuth client binding, binding user's account with GitHub
     * @param oauthId oauth id
     * @param oauthName oauth name
     */
    @PostMapping("/bind/{providerName}")
    fun bind(
        @RequestParam oauthId: String,
        @RequestParam oauthName: String,
        @RequestParam isRebind: Boolean,
        @PathVariable providerName: String,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        oauthService.bindUserWithOAuth(
            oauthId,
            oauthName,
            providerName,
            isRebind
        )
        return ResponseEntity.ok(Response.success("Bind success"))
    }

    /**
     * OAuth client unbinding, unbinding user's account with OAuth provider
     */
    @PostMapping("/unbind/{providerName}")
    fun unbind(
        @PathVariable providerName: String,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        // throw exception if bind failed
        oauthService.unbindUserWithOAuth(providerName)
        return ResponseEntity.ok(Response.success("Unbind success"))
    }
}