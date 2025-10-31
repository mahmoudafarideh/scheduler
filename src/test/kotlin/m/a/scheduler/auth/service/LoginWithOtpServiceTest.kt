package m.a.scheduler.auth.service

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import m.a.scheduler.app.base.testDispatcherProvider
import m.a.scheduler.auth.model.AuthToken
import m.a.scheduler.auth.model.LoginByOtpResult
import m.a.scheduler.auth.model.VerifyOtpResult
import m.a.scheduler.fixtures.phoneNumberFixture
import m.a.scheduler.user.model.User
import m.a.scheduler.user.service.UserService
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class LoginWithOtpServiceTest {

    @MockitoBean
    lateinit var phoneNumberOtpService: PhoneNumberOtpService

    @MockitoBean
    lateinit var userService: UserService

    @MockitoBean
    lateinit var authTokenService: AuthTokenService

    private val coroutineScope = TestScope()

    private fun createService() = LoginWithOtpService(
        phoneNumberOtpService = phoneNumberOtpService,
        userService = userService,
        coroutineDispatcherProvider = coroutineScope.testDispatcherProvider,
        authTokenService = authTokenService
    )

    @Test
    fun `When otp is correct, the user should get logged in`() = coroutineScope.runTest {
        val service = createService()
        mockOtpResult(VerifyOtpResult.Success)
        val user = mockUser()
        val token = mockToken(user)
        val result = service.loginWithOtp(phoneNumberFixture, "123")
        assertEquals(
            LoginByOtpResult.Success(token, user), result
        )
    }

    private suspend fun mockToken(user: User): AuthToken {
        val authToken = AuthToken("at1", "rt2")
        Mockito.`when`(authTokenService.createToken(user)) doReturn authToken
        return authToken
    }

    private suspend fun mockUser(): User {
        val user = User(id = "id", name = "name", phone = phoneNumberFixture, state = User.State.Active)
        Mockito.`when`(userService.getOrCreateUser(phoneNumber = phoneNumberFixture)) doReturn user
        return user
    }

    @Test
    fun `When otp is not correct the user should not get logged in`() = coroutineScope.runTest {
        val service = createService()
        mockOtpResult(VerifyOtpResult.Error.InvalidOtp)
        val result = service.loginWithOtp(phoneNumber = phoneNumberFixture, "123")
        assertEquals(
            LoginByOtpResult.Failure(VerifyOtpResult.Error.InvalidOtp),
            result
        )
        mockOtpResult(VerifyOtpResult.Error.ExpiredOtp)
        val result2 = service.loginWithOtp(phoneNumber = phoneNumberFixture, "123")
        assertEquals(
            LoginByOtpResult.Failure(VerifyOtpResult.Error.ExpiredOtp),
            result2
        )
        mockOtpResult(VerifyOtpResult.Error.NotFound)
        val result3 = service.loginWithOtp(phoneNumber = phoneNumberFixture, "123")
        assertEquals(
            LoginByOtpResult.Failure(VerifyOtpResult.Error.NotFound),
            result3
        )
    }

    private suspend fun mockOtpResult(verifyOtpResult: VerifyOtpResult) {
        Mockito.`when`(phoneNumberOtpService.verifyOtpCode(phoneNumberFixture, "123")) doReturn
                verifyOtpResult
    }
}