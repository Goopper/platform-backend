package top.goopper.platform.controller.oauth

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.oauth.OAuthService

@RestController
@RequestMapping("/oauth")
class CommonController(private val oauthService: OAuthService) {

    private val logger = LoggerFactory.getLogger(CommonController::class.java)

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
        var forwardIps = "null"
        try {
            forwardIps = request.getHeader("X-Forwarded-For")
        } catch (e: NullPointerException) {
            logger.error("OAuth login request error: Forwarded-For is null, provider: $providerName oauthId: $oauthId")
        }

        val jwt = oauthService.authenticate(
            oauthId,
            providerName,
            request.getHeader("User-Agent"),
            forwardIps
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