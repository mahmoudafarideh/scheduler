package m.a.scheduler.auth.service

import m.a.scheduler.user.service.UserService
import org.springframework.stereotype.Service

@Service
class LoginWithOtpService(
    private val phoneNumberOtpService: PhoneNumberOtpService,
    private val userService: UserService,
) {
}