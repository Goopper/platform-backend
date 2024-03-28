package top.goopper.platform.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class GithubService(
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

    /**
     * Check if user exists on GitHub
     * @param username GitHub username
     * @throws Exception if user not found
     */
    fun checkExists(username: String, id: String) {
        val url = "https://api.github.com/users/$username"
        try {
            val response = restTemplate.exchange<String>(url, HttpMethod.GET)
            val user = jacksonObjectMapper().readValue(response.body, Map::class.java)
            if (user["id"].toString() != id) {
                throw Exception("User not found")
            }
        } catch (e: HttpClientErrorException) {
            throw Exception("User not found or http error")
        }
    }

}