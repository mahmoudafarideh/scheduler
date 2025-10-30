package m.a.scheduler.auth.service

import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.auth.database.model.PhoneNumberOtpDto
import m.a.scheduler.auth.database.repository.PhoneNumberOtpRepository
import m.a.scheduler.auth.model.PhoneNumber
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import java.time.Instant
import java.util.*
import kotlin.test.Test

@SpringBootTest
class PhoneNumberOtpServiceTest {

    @MockitoSpyBean
    lateinit var repository: PhoneNumberOtpRepository

    @MockitoSpyBean
    lateinit var timeInstant: TimeInstant

    private fun createService() = PhoneNumberOtpService(repository, timeInstant)

    @Test
    fun `When users tries to login a new 6-len code in 5 minutes should get generated`() {
        Mockito.`when`(timeInstant.now()) doReturn Date(0)
        val service = createService()
        val phoneNumber = PhoneNumber("9192493674")
        service.sendOtp(phoneNumber)
        argumentCaptor<PhoneNumberOtpDto>().apply {
            verify(repository).insert(capture())
            assertEquals(6, firstValue.otp.length)
            assertEquals(3_000, firstValue.expiresAt.time)
        }
    }

}