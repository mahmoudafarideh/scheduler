package m.a.scheduler.auth.service

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.app.base.testDispatcherProvider
import m.a.scheduler.auth.database.model.PhoneNumberDto
import m.a.scheduler.auth.database.model.PhoneNumberOtpDto
import m.a.scheduler.auth.database.repository.PhoneNumberOtpRepository
import m.a.scheduler.auth.database.utils.OtpCodeGenerator
import m.a.scheduler.auth.model.PhoneNumber
import m.a.scheduler.auth.model.VerifyOtpResult
import m.a.scheduler.auth.task.OtpSendTask
import m.a.scheduler.auth.task.OtpTaskInfo
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals

@SpringBootTest
class PhoneNumberOtpServiceTest {

    @MockitoBean
    lateinit var repository: PhoneNumberOtpRepository

    @MockitoBean
    lateinit var otpCodeGenerator: OtpCodeGenerator

    @MockitoBean
    lateinit var otpSendTask: OtpSendTask

    @MockitoBean
    lateinit var timeInstant: TimeInstant

    private val coroutineScope = TestScope()

    private fun createService() = PhoneNumberOtpService(
        phoneNumberOtpRepository = repository,
        timeInstant = timeInstant,
        otpCodeGenerator = otpCodeGenerator,
        coroutineDispatcherProvider = coroutineScope.testDispatcherProvider,
        otpSendTask = otpSendTask
    )

    @BeforeTest
    fun beforeTest() {
        Mockito.`when`(timeInstant.now()) doReturn Date(0)
        Mockito.`when`(otpCodeGenerator.generateOtpCode()) doReturn "123456"
    }

    @Test
    fun `When users tries to login a new 6-len code in 5 minutes should get generated`() = coroutineScope.runTest {
        val service = createService()
        val phoneNumber = PhoneNumber("9192493674")
        service.sendOtp(phoneNumber)
        argumentCaptor<PhoneNumberOtpDto>().apply {
            verify(repository).insert(capture())
            assertEquals("123456", firstValue.otp)
            assertEquals(6, firstValue.otp.length)
            assertEquals(300_000, firstValue.expiresAt.time)
            verify(otpSendTask).schedule(OtpTaskInfo(phoneNumber, "123456"))
        }
    }

    @Test
    fun `Otp code should be generated randomly`() = coroutineScope.runTest {
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
    fun `When user retries to login again, previous codes should get revoked`() = coroutineScope.runTest {
        val service = createService()
        val phoneNumber = PhoneNumber("9192493674")
        val previousCode = generateRandomOtpCode()
        mockPreviousOtpCode(phoneNumber, previousCode)
        val updatedCode = previousCode.copy(status = PhoneNumberOtpDto.Status.Revoked)
        service.sendOtp(phoneNumber)
        argumentCaptor<PhoneNumberOtpDto>().apply {
            verify(repository).save(updatedCode)
        }
    }

    @Test
    fun `When user entered otp correctly, it should be able to login and code get consumed`() = coroutineScope.runTest {
        val service = createService()
        val phoneNumber = PhoneNumber("9192493674")
        val previousCode = generateRandomOtpCode()
        mockPreviousOtpCode(phoneNumber, previousCode)
        val updatedCode = previousCode.copy(status = PhoneNumberOtpDto.Status.Consumed)
        val result = service.verifyOtpCode(phoneNumber, previousCode.otp)
        argumentCaptor<PhoneNumberOtpDto>().apply {
            verify(repository).save(updatedCode)
        }
        assertEquals(VerifyOtpResult.Success, result)
    }

    @Test
    fun `When user entered otp wrongly, it should not be able to login and code not get consumed`() =
        coroutineScope.runTest {
            val service = createService()
            val phoneNumber = PhoneNumber("9192493674")
            val previousCode = generateRandomOtpCode()
            val updatedCode = previousCode.copy(status = PhoneNumberOtpDto.Status.Consumed)

            mockPreviousOtpCode(phoneNumber, previousCode)
            val result = service.verifyOtpCode(phoneNumber, "000000")
            assertEquals(VerifyOtpResult.Error.InvalidOtp, result)

            mockPreviousOtpCode(phoneNumber, previousCode.copy(expiresAt = Date(-10)))
            val result2 = service.verifyOtpCode(phoneNumber, previousCode.otp)
            assertEquals(VerifyOtpResult.Error.ExpiredOtp, result2)

            mockPreviousOtpCode(phoneNumber, null)
            val result3 = service.verifyOtpCode(phoneNumber, "000000")
            assertEquals(VerifyOtpResult.Error.NotFound, result3)

            argumentCaptor<PhoneNumberOtpDto>().apply {
                verify(repository, times(0)).save(updatedCode)
            }
        }

    @Test
    fun `When user retries to login again and previous code is not pending, previous codes should not get revoked`() =
        coroutineScope.runTest {
            val service = createService()
            val phoneNumber = PhoneNumber("9192493674")
            val previousCode = generateRandomOtpCode().copy(status = PhoneNumberOtpDto.Status.Consumed)
            mockPreviousOtpCode(phoneNumber, previousCode)
            val updatedCode = previousCode.copy(status = PhoneNumberOtpDto.Status.Revoked)
            service.sendOtp(phoneNumber)
            argumentCaptor<PhoneNumberOtpDto>().apply {
                verify(repository, times(0)).save(updatedCode)
            }
        }

    private fun mockPreviousOtpCode(
        phoneNumber: PhoneNumber,
        previousCode: PhoneNumberOtpDto? = null
    ) {
        Mockito.`when`(
            repository.findFirstByPhoneOrderByCreatedAtDesc(PhoneNumberDto(phoneNumber.number, "98"))
        ) doReturn previousCode
    }

    private fun generateRandomOtpCode() = PhoneNumberOtpDto(
        id = ObjectId(),
        phone = PhoneNumberDto("9192493674", "98"),
        otp = "654321",
        createdAt = timeInstant.now(),
        updatedAt = timeInstant.now(),
        expiresAt = timeInstant.now()
    )

}