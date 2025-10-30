package m.a.scheduler.auth.controller

import m.a.scheduler.auth.controller.request.RegisterRequest
import jakarta.validation.Valid
import m.a.scheduler.app.security.PublicApi
import m.a.scheduler.auth.controller.request.toPhoneNumber
import m.a.scheduler.auth.service.PhoneNumberOtpService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val otpService: PhoneNumberOtpService
) {
    @PublicApi
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): String {
        otpService.sendOtp(request.toPhoneNumber())
        return request.phoneNumber
    }
}