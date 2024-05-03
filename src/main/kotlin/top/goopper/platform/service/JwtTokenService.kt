package top.goopper.platform.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys.hmacShaKeyFor
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import top.goopper.platform.dao.UserDAO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.pojo.JwtSubject
import java.util.*

/**
 * JWT Auth TOKEN 包含：
 * 1. uid 用户ID
 * 2. number 用户编号
 * 3. name 用户名
 * 4. roleName 用户角色
 * 5. browserName 浏览器名称
 * 6. deviceName 设备名称
 * 7. ua UserAgent
 * JWT Auth TOKEN 有效期 1h
 * @see JwtSubject
 */
@Component
class JwtTokenService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val userDAO: UserDAO
) {

    // secret key for jwt
    private val secretKey = "50b989ddef493a58ee52e05d9110c9d8b0604dd16e9e3660fecd40cfd0feabd9"
    // 1h
    private val validityInMilliseconds: Long = 3600000
    private val validator = Jwts.parser().verifyWith(hmacShaKeyFor(secretKey.toByteArray())).build()

    /**
     * Create token
     * @param subject payload data
     * @see JwtSubject
     * @return jwt token
     */
    fun storeAuthToken(subject: JwtSubject): String {
        val now = Date()
        val token = Jwts.builder()
            .subject(jacksonObjectMapper().writeValueAsString(subject))
            .issuedAt(now)
            .signWith(hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
        val key = ("auth:${subject.uid}:${subject.hashCode()}").toByteArray()
        redisTemplate.execute {
            it.stringCommands().set(key, token.toByteArray())
            it.keyCommands().expire(key, validityInMilliseconds / 1000)
        }
        return token
    }

    /**
     * Renew token expiration
     * @param token jwt token
     */
    fun renewAuthTokenExpiration(token: String) {
        val subject = getSubject(token)
        val key = ("auth:${subject.uid}:${subject.hashCode()}").toByteArray()
        redisTemplate.execute {
            it.keyCommands().expire(key, validityInMilliseconds / 1000)
        }
    }

    /**
     * Remove token from redis
     * @param token jwt token
     * @throws Exception if token is invalid
     */
    fun removeAuthToken(token: String) {
        val subject = getSubject(token)
        val key = ("auth:${subject.uid}:${subject.hashCode()}").toByteArray()
        redisTemplate.execute {
            it.keyCommands().del(key)
        }
    }

    /**
     * Remove token from redis with token id
     * @param tokenId jwt token id
     * @throws Exception if token is invalid
     */
    fun removeAuthToken(tokenId: Int) {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val key = ("auth:${user.id}:$tokenId").toByteArray()
        redisTemplate.execute {
            it.keyCommands().del(key)
        }
    }

    /**
     * Validate token
     * @param token jwt token
     * @return true if token is valid
     * @throws Exception if token is invalid or expired
     */
    fun validateAuthToken(token: String): JwtSubject {
        val subject = getSubject(token)
        val key = ("auth:${subject.uid}:${subject.hashCode()}").toByteArray()
        redisTemplate.execute {
            val auth = it.stringCommands().get(key)
            if (auth == null || String(auth) != token) {
                throw Exception("Token is invalid or expired.")
            }
        }
        return subject
    }

    /**
     * Get payload data from token
     * @param token jwt token
     * @return payload data
     * @throws Exception if token is invalid
     * @see JwtSubject
     */
    fun getSubject(token: String): JwtSubject {
        val subject = getPayload(token).subject
        val payloadDTO: JwtSubject = jacksonObjectMapper().readValue(subject)
        return payloadDTO
    }

    /**
     * Get authentication from jwt token
     * @param payload payload data
     * @see JwtSubject
     * @return Authentication
     */
    fun getAuthenticationFromJWT(payload: JwtSubject): Authentication {
        val user = userDAO.loadUserByNumber(payload.number)
        return UsernamePasswordAuthenticationToken(
            user,
            user.id,
            listOf(GrantedAuthority { user.roleName })
        )
    }

    /**
     * Get payload from token
     * @param token jwt token
     */
    fun getPayload(token: String): Claims {
        val subject = validator.parseSignedClaims(token).payload
        return subject
    }

    /**
     * When important security info changes
     * Revoke all tokens by user id, clear the user's login status
     */
    fun revokeAllTokensByUserId(id: Int) {
        redisTemplate.execute {
            val pattern = "auth:$id:*"
            val keys = it.keyCommands().keys(pattern.toByteArray()).orEmpty().toTypedArray()
            // revoke all tokens
            it.keyCommands().del(*keys)
        }
    }

}