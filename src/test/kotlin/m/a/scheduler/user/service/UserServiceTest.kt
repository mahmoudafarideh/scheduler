package m.a.scheduler.user.service

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.app.base.testDispatcherProvider
import m.a.scheduler.auth.database.model.PhoneNumberDto
import m.a.scheduler.auth.database.model.toPhoneNumberDto
import m.a.scheduler.auth.service.GetAuthUserId
import m.a.scheduler.fixtures.phoneNumberFixture
import m.a.scheduler.user.database.model.UserDto
import m.a.scheduler.user.database.model.toUser
import m.a.scheduler.user.database.repository.UserRepository
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class UserServiceTest {

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var getAuthUserId: GetAuthUserId

    @MockitoBean
    private lateinit var timeInstant: TimeInstant

    private val coroutineScope = TestScope()


    @BeforeTest
    fun setup() {
        Mockito.`when`(timeInstant.now()) doReturn Date(0)
    }

    private fun createService(): UserService = UserService(
        userRepository = userRepository,
        timeInstant = timeInstant,
        coroutineDispatcherProvider = coroutineScope.testDispatcherProvider,
        getAuthUserId = getAuthUserId
    )

    @Test
    fun `When there is a user registered with phone, it should return that user`() = coroutineScope.runTest {
        val service = createService()
        val phoneDto = phoneNumberFixture.toPhoneNumberDto()
        val mockUser = mockUser(phoneDto).toUser()
        val user = service.getOrCreateUser(phoneNumberFixture)
        assertEquals(mockUser, user)
    }

    @Test
    fun `When there is no user registered with phone, it should create and return that user`() =
        coroutineScope.runTest {
            val phoneDto = phoneNumberFixture.toPhoneNumberDto()
            Mockito.`when`(userRepository.findUserByPhone(phone = phoneDto)) doReturn null
            val newUser = phoneDto.toUser()
            Mockito.`when`(userRepository.save(any())) doReturn newUser

            val service = createService()
            val user = service.getOrCreateUser(phoneNumberFixture)
            assertEquals(newUser.toUser(), user)
        }

    private fun mockUser(phoneDto: PhoneNumberDto): UserDto {
        val userDto = phoneDto.toUser()
        Mockito.`when`(userRepository.findUserByPhone(phone = phoneDto)) doReturn userDto
        return userDto
    }

    private fun PhoneNumberDto.toUser(): UserDto =
        UserDto(phone = this, createdAt = timeInstant.now(), updatedAt = timeInstant.now(), name = "")

}