package m.a.scheduler.auth.model

import m.a.scheduler.user.model.User

sealed class LoginByOtpResult {
    data class Success(
        val authToken: AuthToken,
        val user: User
    ) : LoginByOtpResult()

    data class Failure(val error: VerifyOtpResult.Error) : LoginByOtpResult()
}