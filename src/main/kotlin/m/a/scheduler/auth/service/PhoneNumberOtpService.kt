package m.a.scheduler.auth.service

import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.auth.database.model.PhoneNumberOtpDto
import m.a.scheduler.auth.database.repository.PhoneNumberOtpRepository
import m.a.scheduler.auth.model.PhoneNumber
import org.springframework.stereotype.Service
import java.util.*

@Service
class PhoneNumberOtpService(
    private val phoneNumberOtpRepository: PhoneNumberOtpRepository,
    private val timeInstant: TimeInstant
) {
    fun sendOtp(phoneNumber: PhoneNumber) {
        phoneNumberOtpRepository.insert(
            PhoneNumberOtpDto(
                phoneNumber = phoneNumber.number,
                countryCode = phoneNumber.countryCode.countryCode,
                otp = "123456",
                createdAt = timeInstant.now(),
                updatedAt = timeInstant.now(),
                expiresAt = Date(timeInstant.now().time + 3_000)
            )
        )
    }
}