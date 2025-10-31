package m.a.scheduler.auth.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.withContext
import m.a.scheduler.app.base.CoroutineDispatcherProvider
import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.auth.database.model.RefreshTokenDto
import m.a.scheduler.auth.database.repository.RefreshTokenRepository
import m.a.scheduler.auth.model.AuthToken
import m.a.scheduler.user.model.User
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.util.*

private const val accessTokenKey = "access-token"
private const val refreshTokenKey = "refresh-token"

@Service
class AuthTokenService(
    @Value("\${jwt.secret}") private val jwtSecret: String,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val timeInstant: TimeInstant
) {

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    }
    private val accessTokenValidityMs = 60L * 60L * 1000L
    private val refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1000L

    suspend fun createToken(user: User): AuthToken {
        val accessToken = generateToken(userId = user.id, accessTokenKey, accessTokenValidityMs)
        val refreshToken = generateToken(userId = user.id, refreshTokenKey, refreshTokenValidityMs)
        withContext(coroutineDispatcherProvider.io) {
            refreshTokenRepository.save(
                RefreshTokenDto(
                    userId = ObjectId(user.id),
                    hashedToken = hashToken(refreshToken),
                    expiresAt = Date(timeInstant.now().time + refreshTokenValidityMs),
                    createdAt = timeInstant.now()
                )
            )
        }
        return AuthToken(accessToken, refreshToken)
    }

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == accessTokenKey
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == refreshTokenKey
    }

    fun getUserIdFromToken(token: String): String {
        val claims = parseAllClaims(token)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid token.")
        return claims.subject
    }

    private fun generateToken(userId: String, type: String, expiry: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = if (token.startsWith("Bearer ")) {
            token.removePrefix("Bearer ")
        } else token
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            null
        }
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

}