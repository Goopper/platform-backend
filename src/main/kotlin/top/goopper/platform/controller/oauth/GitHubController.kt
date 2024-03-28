package top.goopper.platform.controller.oauth

import com.fasterxml.jackson.module.kotlin.jsonMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.GithubService

@RestController
@RequestMapping("/oauth/github")
class GitHubController(
    private val gitHubService: GithubService,
) {
    /**
     * Build GitHub OAuth URL
     * @param redirectUrl redirect url
     */
    @GetMapping("/url")
    fun url(@RequestParam redirectUrl: String): ResponseEntity<Response> {
        val url = gitHubService.getOAuthUrl(redirectUrl)
        return ResponseEntity.ok(Response.success(url))
    }

    /**
     * Front end need request this API with the code from GitHub OAuth callback
     * @param code GitHub OAuth code
     */
    @GetMapping("/auth")
    fun auth(@RequestParam code: String): ResponseEntity<Response> {
        val accessToken = gitHubService.getAccessToken(code)
        val userInfo = gitHubService.getUserInfo(accessToken)
        val githubUser = jsonMapper().readValue(userInfo, Map::class.java)
        return ResponseEntity.ok(Response.success(githubUser))
    }
}