package m.a.scheduler.auth.service

import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.auth.database.model.PhoneNumberOtpDto
import m.a.scheduler.auth.database.repository.PhoneNumberOtpRepository
import m.a.scheduler.auth.database.utils.OtpCodeGenerator
import m.a.scheduler.auth.model.PhoneNumber
import org.springframework.stereotype.Service
import java.util.*

@Service
class PhoneNumberOtpService(
    private val phoneNumberOtpRepository: PhoneNumberOtpRepository,
    private val timeInstant: TimeInstant,
    private val otpCodeGenerator: OtpCodeGenerator
) {
    fun sendOtp(phoneNumber: PhoneNumber) {
        revokeCurrentCode(phoneNumber)
        val phoneNumberOtpDto = makeNewOtpCode(phoneNumber)
        phoneNumberOtpRepository.insert(phoneNumberOtpDto)
    }

    private fun revokeCurrentCode(phoneNumber: PhoneNumber) {
        val previousCodes = phoneNumberOtpRepository.findFirstByPhoneNumberAndCountryCodeOrderByCreatedAtDesc(
            phoneNumber = phoneNumber.number,
            countryCode = phoneNumber.countryCode.countryCode
        )?.takeIf {
            it.status == PhoneNumberOtpDto.Status.New
        }
        previousCodes?.let {
            phoneNumberOtpRepository.save(it.copy(status = PhoneNumberOtpDto.Status.Revoked))
        }
    }

    private fun makeNewOtpCode(phoneNumber: PhoneNumber) = PhoneNumberOtpDto(
        phoneNumber = phoneNumber.number,
        countryCode = phoneNumber.countryCode.countryCode,
        otp = otpCodeGenerator.generateOtpCode(),
        createdAt = timeInstant.now(),
        updatedAt = timeInstant.now(),
        expiresAt = Date(timeInstant.now().time + 3_000)
    )
}