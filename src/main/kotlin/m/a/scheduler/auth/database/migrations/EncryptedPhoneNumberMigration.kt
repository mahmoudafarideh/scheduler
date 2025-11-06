package m.a.scheduler.auth.database.migrations

import com.kuliginstepan.mongration.annotation.Changelog
import com.kuliginstepan.mongration.annotation.Changeset
import m.a.scheduler.auth.database.utils.PhoneNumberCrypto
import m.a.scheduler.auth.model.PhoneNumber
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate


@Changelog
class EncryptedPhoneNumberMigration(
    private val phoneNumberCrypto: PhoneNumberCrypto
) {
    @Changeset(order = 1, id = "encrypt_phone_number", author = "Mahmoud")
    fun phoneOtpCryptoChangeSet(template: MongoTemplate) {
        template.findAll(Document::class.java, "phone_number_otp").forEach { phoneOtp ->
            val phone = getEncryptedPhoneNumber(phoneOtp)
            phoneOtp["phone"] = phone
            template.save(phoneOtp, "phone_number_otp")
        }
    }

    @Changeset(order = 2, id = "encrypted_user_phone_number", author = "Mahmoud")
    fun userPhoneCryptoChangeSet(template: MongoTemplate) {
        template.findAll(Document::class.java, "users").forEach { phoneOtp ->
            val phone = getEncryptedPhoneNumber(phoneOtp)
            phoneOtp["phone"] = phone
            template.save(phoneOtp, "users")
        }
    }

    private fun getEncryptedPhoneNumber(phoneOtp: Document): Document {
        val phone = phoneOtp.get("phone", Document::class.java)
        val phoneNumber = phone.get("phoneNumber", String::class.java)
        val countryCode = phone.get("countryCode", String::class.java)
        val encryptedPhoneNumber = phoneNumberCrypto.encrypt(
            PhoneNumber(
                phoneNumber,
                PhoneNumber.CountryCode.entries.first { it.countryCode == countryCode }
            )
        )
        phone["phoneNumber"] = encryptedPhoneNumber.phoneNumber
        phone["countryCode"] = encryptedPhoneNumber.countryCode
        return phone
    }
}