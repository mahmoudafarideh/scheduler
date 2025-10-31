package m.a.scheduler.auth.service

import m.a.scheduler.auth.model.AuthToken
import m.a.scheduler.user.model.User
import org.springframework.stereotype.Service

@Service
class AuthTokenService {
    suspend fun createToken(user: User): AuthToken = TODO()
}