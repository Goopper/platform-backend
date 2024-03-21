package top.goopper.platform.service

import eu.bitwalker.useragentutils.UserAgent
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import top.goopper.platform.dao.UserDAO
import top.goopper.platform.dto.JwtSubjectDTO

@Service
class GitHubService(
    private val restTemplate: RestTemplate,
    private val userDAO: UserDAO,
    private val jwtTokenService: JwtTokenService
) {

    fun getAccessToken(code: String): String {
        val url = "https://github.com/login/oauth/access_token"
        val params = mapOf(
            "client_id" to "47d838b6baed219d15e2",
            "client_secret" to "0df7c4d195c889a002afbbda4bd984801dd51975",
            "code" to code)
        val httpEntity = HttpEntity(params, HttpHeaders())
        val response = restTemplate.exchange<String>(url, HttpMethod.POST, httpEntity)
        return response.body?.split("&")?.find { it.startsWith("access_token=") }?.split("=")?.get(1) ?: ""
    }

    fun getUserInfo(accessToken: String): String {
        val url = "https://api.github.com/user"
        val headers = HttpHeaders().apply {
            add("Authorization", "Bearer $accessToken")
        }
        val httpEntity = HttpEntity<String>(headers)
        val response = restTemplate.exchange<String>(url, HttpMethod.GET, httpEntity)
        return response.body ?: ""
    }

    /**
     * Check if the user's oauth exists in the database
     * @param githubID user id from GitHub
     * @param userAgent user agent
     * @return jwt token
     */
    fun authenticate(githubID: String, userAgentStr: String): String {
        val userAgent = UserAgent.parseUserAgentString(userAgentStr)
        val user = userDAO.loadUserByOAuthId(githubID)
        // save to SecurityContext
        val authentication =
            UsernamePasswordAuthenticationToken(user, user.number, listOf(GrantedAuthority { user.roleName }))
        SecurityContextHolder.getContext().authentication = authentication
        // create jwt and return
        val jwt = jwtTokenService.storeAuthToken(
            JwtSubjectDTO(
                uid = user.id,
                number = user.number,
                name = user.name,
                roleName = user.roleName,
                browserName = userAgent.browser.name,
                deviceName = userAgent.operatingSystem.deviceType.name,
                ua = userAgentStr
            )
        )
        return jwt
    }

}