package m.a.scheduler.auth.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

interface GetAuthUserId {
    fun execute(): String
}

@Component
class GetAuthUserIdImpl : GetAuthUserId {
    override fun execute(): String {
        return SecurityContextHolder.getContext().authentication.principal as String
    }
}