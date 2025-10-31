package m.a.scheduler.auth.service

import kotlinx.coroutines.withContext
import m.a.scheduler.app.base.CoroutineDispatcherProvider
import m.a.scheduler.auth.model.LoginByOtpResult
import m.a.scheduler.auth.model.PhoneNumber
import m.a.scheduler.auth.model.VerifyOtpResult
import m.a.scheduler.user.service.UserService
import org.springframework.stereotype.Service

@Service
class LoginWithOtpService(
    private val phoneNumberOtpService: PhoneNumberOtpService,
    private val userService: UserService,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val authTokenService: AuthTokenService
) {
    suspend fun loginWithOtp(phoneNumber: PhoneNumber, otp: String): LoginByOtpResult {
        return withContext(coroutineDispatcherProvider.io) {
            val otpVerify = phoneNumberOtpService.verifyOtpCode(phoneNumber, otp)
            if (otpVerify is VerifyOtpResult.Error) {
                return@withContext LoginByOtpResult.Failure(otpVerify)
            }
            val user = userService.getOrCreateUser(phoneNumber)
            return@withContext LoginByOtpResult.Success(
                user = user,
                authToken = authTokenService.createToken(user)
            )
        }
    }
}