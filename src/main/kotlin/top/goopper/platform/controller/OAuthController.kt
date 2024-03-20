package top.goopper.platform.controller

import com.fasterxml.jackson.module.kotlin.jsonMapper
import eu.bitwalker.useragentutils.UserAgent
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.service.JwtTokenService
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.GitHubService
import top.goopper.platform.service.OAuthService

@RestController
@RequestMapping("/oauth")
class OAuthController(
    private val gitHubService: GitHubService,
    private val oauthService: OAuthService,
) {
    /**
     * GitHub OAuth callback
     * @param code GitHub OAuth code
     */
    @GetMapping("/github/callback")
    fun oauthGithubCallback(@RequestParam code: String): ResponseEntity<Response> {
        val accessToken = gitHubService.getAccessToken(code)
        val userInfo = gitHubService.getUserInfo(accessToken)
        val githubUser = jsonMapper().readValue(userInfo, Map::class.java)
        return ResponseEntity.ok(Response.success(githubUser))
    }

    /**
     * GitHub OAuth client login
     * @param oauthId oauth id
     * @param request http request, provide user agent
     */
    @PostMapping("/github/login")
    fun oauthGithubLogin(
        @RequestParam oauthId: String,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        val jwt = gitHubService.authenticate(
            oauthId,
            request.getHeader("User-Agent")
        )
        return ResponseEntity.ok(Response.success(jwt))
    }

    /**
     * GitHub OAuth client binding, binding user's account with GitHub
     * @param oauthId oauth id
     * @param oauthName oauth name
     */
    @PostMapping("/github/bind")
    fun oauthGithubBind(
        @RequestParam oauthId: String,
        @RequestParam oauthName: String,
        request: HttpServletRequest
    ): ResponseEntity<Response> {
        val result = oauthService.bindUserWithOAuth(
            oauthId,
            oauthName,
            "github"
        )
        return if (result) {
            ResponseEntity.ok(Response.success("Bind success"))
        } else {
            ResponseEntity.ok(Response.error(400, "Bind failed"))
        }
    }
}