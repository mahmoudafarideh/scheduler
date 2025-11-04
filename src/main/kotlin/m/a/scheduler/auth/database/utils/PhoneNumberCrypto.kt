package m.a.scheduler.auth.database.utils

import m.a.scheduler.auth.database.model.EncryptedPhoneNumberDto
import m.a.scheduler.auth.model.PhoneNumber
import org.springframework.stereotype.Component

@Component
class PhoneNumberCrypto {
    fun encrypt(phoneNumber: PhoneNumber): EncryptedPhoneNumberDto {
        TODO()
    }

    fun decrypt(phoneNumber: EncryptedPhoneNumberDto): PhoneNumber {
        TODO()
    }
}