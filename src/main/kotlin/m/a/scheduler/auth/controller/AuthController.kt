package m.a.scheduler.auth.controller

import jakarta.validation.Valid
import m.a.scheduler.app.security.PublicApi
import m.a.scheduler.auth.controller.request.RefreshTokenRequest
import m.a.scheduler.auth.controller.request.RegisterRequest
import m.a.scheduler.auth.controller.request.VerifyOtpRequest
import m.a.scheduler.auth.controller.request.toPhoneNumber
import m.a.scheduler.auth.controller.response.*
import m.a.scheduler.auth.controller.response.toInvalidArgumentException
import m.a.scheduler.auth.controller.response.toLoginResponse
import m.a.scheduler.auth.model.LoginByOtpResult
import m.a.scheduler.auth.service.AuthTokenService
import m.a.scheduler.auth.service.LoginWithOtpService
import m.a.scheduler.auth.service.PhoneNumberOtpService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val otpService: PhoneNumberOtpService,
    private val loginWithOtpService: LoginWithOtpService,
    private val authTokenService: AuthTokenService
) {

    @PublicApi
    @PostMapping("/register")
    suspend fun register(
        @Valid @RequestBody request: RegisterRequest
    ): OtpResponse {
        otpService.sendOtp(request.toPhoneNumber())
        return OtpResponse.success(60)
    }

    @PublicApi
    @PostMapping("/verify-otp")
    suspend fun verifyOtp(
        @Valid @RequestBody request: VerifyOtpRequest
    ): UserLoginResponse {
        return when (val verifyOtpRequest = loginWithOtpService.loginWithOtp(request.toPhoneNumber(), request.otp)) {
            is LoginByOtpResult.Failure -> throw verifyOtpRequest.error.toInvalidArgumentException()
            is LoginByOtpResult.Success -> verifyOtpRequest.user.toLoginResponse(verifyOtpRequest.authToken)
        }
    }

    @PublicApi
    @PostMapping("/refresh-token")
    suspend fun refreshToken(
        @RequestBody body: RefreshTokenRequest
    ): TokenResponse {
        val token = authTokenService.refreshToken(body.refreshToken)
        return token.toTokenResponse()
    }
}