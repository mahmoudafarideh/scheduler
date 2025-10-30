package m.a.scheduler.auth.service

import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.auth.database.model.PhoneNumberOtpDto
import m.a.scheduler.auth.database.repository.PhoneNumberOtpRepository
import m.a.scheduler.auth.database.utils.OtpCodeGenerator
import m.a.scheduler.auth.model.PhoneNumber
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals

@SpringBootTest
class PhoneNumberOtpServiceTest {

    @MockitoSpyBean
    lateinit var repository: PhoneNumberOtpRepository

    @MockitoSpyBean
    lateinit var otpCodeGenerator: OtpCodeGenerator

    @MockitoSpyBean
    lateinit var timeInstant: TimeInstant

    private fun createService() = PhoneNumberOtpService(repository, timeInstant, otpCodeGenerator)

    @BeforeTest
    fun beforeTest() {
        Mockito.`when`(timeInstant.now()) doReturn Date(0)
        Mockito.`when`(otpCodeGenerator.generateOtpCode()) doReturn "123456"
    }

    @Test
    fun `When users tries to login a new 6-len code in 5 minutes should get generated`() {
        val service = createService()
        val phoneNumber = PhoneNumber("9192493674")
        service.sendOtp(phoneNumber)
        argumentCaptor<PhoneNumberOtpDto>().apply {
            verify(repository).insert(capture())
            assertEquals("123456", firstValue.otp)
            assertEquals(6, firstValue.otp.length)
            assertEquals(3_000, firstValue.expiresAt.time)
        }
    }

    @Test
    fun `Otp code should be generated randomly`() {
        Mockito.`when`(timeInstant.now()) doReturn Date(0)
        val service = createService()
        val phoneNumber = PhoneNumber("9192493674")
        service.sendOtp(phoneNumber)

        Mockito.`when`(otpCodeGenerator.generateOtpCode()) doReturn "654321"
        service.sendOtp(phoneNumber)

        val captor = argumentCaptor<PhoneNumberOtpDto>()
        verify(repository, times(2)).insert(captor.capture())

        val first = captor.allValues[0]
        val second = captor.allValues[1]

        assertEquals(6, first.otp.length)
        assertEquals(6, second.otp.length)
        assertNotEquals(first.otp, second.otp, "Two consecutive OTPs should differ")
    }

    @Test
    fun `When user retries to login again, previous codes should get revoked`() {
        val service = createService()
        val phoneNumber = PhoneNumber("9192493674")
        val previousCode = generateRandomOtpCode()
        Mockito.`when`(
            repository.findAllByPhoneNumberAndCountryCode(phoneNumber.number, "98")
        ) doReturn listOf(previousCode)
        val updatedCode = previousCode.copy(status = PhoneNumberOtpDto.Status.Revoked)
        service.sendOtp(phoneNumber)
        argumentCaptor<PhoneNumberOtpDto>().apply {
            verify(repository).save(updatedCode)
        }
    }

    private fun generateRandomOtpCode() = PhoneNumberOtpDto(
        id = ObjectId(),
        phoneNumber = "9192493674",
        countryCode = "98",
        otp = "654321",
        createdAt = timeInstant.now(),
        updatedAt = timeInstant.now(),
        expiresAt = timeInstant.now()
    )

}