package m.a.scheduler.auth.database.utils

import m.a.scheduler.auth.database.model.EncryptedPhoneNumberDto
import m.a.scheduler.auth.model.PhoneNumber
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class PhoneNumberCrypto(
    @Value("\${phone.crypto.config.secret-key}")
    phoneCryptoSecretKey: String
) {
    private val algorithm = "AES"
    private val keySpec = SecretKeySpec(
        Base64.getDecoder().decode(phoneCryptoSecretKey),
        algorithm
    )

    fun encrypt(phoneNumber: PhoneNumber): EncryptedPhoneNumberDto {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypted = cipher.doFinal(phoneNumber.number.toByteArray(Charsets.UTF_8))
        return EncryptedPhoneNumberDto(
            phoneNumber = Base64.getEncoder().encodeToString(encrypted),
            countryCode = phoneNumber.countryCode.name
        )
    }

    fun decrypt(phoneNumber: EncryptedPhoneNumberDto): PhoneNumber {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decoded = Base64.getDecoder().decode(phoneNumber.phoneNumber)
        val decrypted = cipher.doFinal(decoded)
        return PhoneNumber(String(decrypted, Charsets.UTF_8), PhoneNumber.CountryCode.valueOf(phoneNumber.countryCode))
    }
}