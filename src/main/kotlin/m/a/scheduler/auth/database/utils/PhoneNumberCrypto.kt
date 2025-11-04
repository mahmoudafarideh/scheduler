package m.a.scheduler.auth.database.utils

import m.a.scheduler.auth.database.model.PhoneNumberDto
import m.a.scheduler.auth.model.PhoneNumber
import org.springframework.stereotype.Component

@Component
class PhoneNumberCrypto {
    fun encrypt(phoneNumber: PhoneNumber): PhoneNumberDto {
        TODO()
    }

    fun decrypt(phoneNumber: PhoneNumberDto): PhoneNumber {
        TODO()
    }
}