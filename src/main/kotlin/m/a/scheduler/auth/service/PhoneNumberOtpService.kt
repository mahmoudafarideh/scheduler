package m.a.scheduler.auth.service

import kotlinx.coroutines.withContext
import m.a.scheduler.app.base.CoroutineDispatcherProvider
import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.auth.database.model.PhoneNumberOtpDto
import m.a.scheduler.auth.database.repository.PhoneNumberOtpRepository
import m.a.scheduler.auth.database.utils.OtpCodeGenerator
import m.a.scheduler.auth.database.utils.PhoneNumberCrypto
import m.a.scheduler.auth.model.PhoneNumber
import m.a.scheduler.auth.model.VerifyOtpResult
import m.a.scheduler.auth.task.OtpSendTask
import m.a.scheduler.auth.task.OtpTaskInfo
import org.springframework.stereotype.Service
import java.util.*

@Service
class PhoneNumberOtpService(
    private val phoneNumberOtpRepository: PhoneNumberOtpRepository,
    private val timeInstant: TimeInstant,
    private val otpCodeGenerator: OtpCodeGenerator,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val otpSendTask: OtpSendTask,
    private val phoneNumberCrypto: PhoneNumberCrypto
) {
    suspend fun sendOtp(phoneNumber: PhoneNumber) {
        withContext(coroutineDispatcherProvider.io) {
            revokeCurrentCode(phoneNumber)
            val phoneNumberOtpDto = makeNewOtpCode(phoneNumber)
            phoneNumberOtpRepository.insert(phoneNumberOtpDto)
            otpSendTask.schedule(OtpTaskInfo(phoneNumber, phoneNumberOtpDto.otp))
        }
    }

    suspend fun verifyOtpCode(
        phoneNumber: PhoneNumber,
        otp: String
    ): VerifyOtpResult {
        return withContext(coroutineDispatcherProvider.io) {
            val previousCode = getPreviousPendingCode(phoneNumber) ?: return@withContext VerifyOtpResult.Error.NotFound
            if (previousCode.otp != otp) {
                return@withContext VerifyOtpResult.Error.InvalidOtp
            }
            if (previousCode.expiresAt < timeInstant.now()) {
                return@withContext VerifyOtpResult.Error.ExpiredOtp
            }
            phoneNumberOtpRepository.save(previousCode.copy(status = PhoneNumberOtpDto.Status.Consumed))
            return@withContext VerifyOtpResult.Success
        }
    }

    private suspend fun revokeCurrentCode(phoneNumber: PhoneNumber) {
        withContext(coroutineDispatcherProvider.io) {
            val previousCode = getPreviousPendingCode(phoneNumber)
            previousCode?.let {
                phoneNumberOtpRepository.save(it.copy(status = PhoneNumberOtpDto.Status.Revoked))
            }
        }
    }

    private fun getPreviousPendingCode(phoneNumber: PhoneNumber): PhoneNumberOtpDto? {
        return phoneNumberOtpRepository.findFirstByPhoneOrderByCreatedAtDesc(
            phone = phoneNumberCrypto.encrypt(phoneNumber)
        )?.takeIf {
            it.status == PhoneNumberOtpDto.Status.Pending
        }
    }

    private fun makeNewOtpCode(phoneNumber: PhoneNumber) = PhoneNumberOtpDto(
        phone = phoneNumberCrypto.encrypt(phoneNumber),
        otp = otpCodeGenerator.generateOtpCode(),
        createdAt = timeInstant.now(),
        updatedAt = timeInstant.now(),
        expiresAt = Date(timeInstant.now().time + 300_000)
    )
}