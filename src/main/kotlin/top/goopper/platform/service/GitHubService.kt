package top.goopper.platform.service

import eu.bitwalker.useragentutils.UserAgent
import org.springframework.beans.factory.annotation.Value
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
) {

    @Value("\${spring.security.oauth2.client.registration.github.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.github.client-secret}")
    private lateinit var clientSecret: String

    fun getAccessToken(code: String): String {
        val url = "https://github.com/login/oauth/access_token"
        val params = mapOf(
            "client_id" to clientId,
            "client_secret" to clientSecret,
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
     * Get OAuth URL
     * @param redirectUrl redirect url
     * @return OAuth URL
     */
    fun getOAuthUrl(redirectUrl: String): String {
        return "https://github.com/login/oauth/authorize?" +
                "&client_id=$clientId" +
                "&redirect_uri=$redirectUrl"
    }

}