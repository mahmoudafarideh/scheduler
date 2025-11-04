package m.a.scheduler.auth.database.utils

import m.a.scheduler.auth.database.model.EncryptedPhoneNumberDto
import m.a.scheduler.fixtures.phoneNumberFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class PhoneNumberCryptoTest {

    private fun createCrypto() = PhoneNumberCrypto(
        phoneCryptoSecretKey = "cmVjb2duaXplbmV3b2JqZWN0aXRzZWxmYm9hdHdpbmQ="
    )

    @Test
    fun `When someone wants to encrypt a phone number, It should return encrypted phone dto`() {
        val crypto = createCrypto()
        val expected = EncryptedPhoneNumberDto("l5d9ziPPwGWK4GweI8GImA==", "Iran")
        assertEquals(expected, crypto.encrypt(phoneNumberFixture))
    }

    @Test
    fun `When someone wants to decrypt an encrypted phone number, It should return phone number`() {
        val crypto = createCrypto()
        assertEquals(phoneNumberFixture, crypto.decrypt(EncryptedPhoneNumberDto("l5d9ziPPwGWK4GweI8GImA==", "Iran")))
    }
}