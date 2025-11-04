package m.a.scheduler.user.service

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.app.base.testDispatcherProvider
import m.a.scheduler.auth.database.model.EncryptedPhoneNumberDto
import m.a.scheduler.auth.database.utils.PhoneNumberCrypto
import m.a.scheduler.auth.service.GetAuthUserId
import m.a.scheduler.fixtures.encryptedPhoneNumberDtoFixture
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

    @MockitoBean
    private lateinit var phoneNumberCrypto: PhoneNumberCrypto

    private val coroutineScope = TestScope()

    @BeforeTest
    fun setup() {
        Mockito.`when`(timeInstant.now()) doReturn Date(0)
        Mockito.`when`(phoneNumberCrypto.encrypt(any())) doReturn encryptedPhoneNumberDtoFixture
        Mockito.`when`(phoneNumberCrypto.decrypt(any())) doReturn phoneNumberFixture
    }

    private fun createService(): UserService = UserService(
        userRepository = userRepository,
        timeInstant = timeInstant,
        coroutineDispatcherProvider = coroutineScope.testDispatcherProvider,
        getAuthUserId = getAuthUserId,
        phoneNumberCrypto = phoneNumberCrypto
    )

    @Test
    fun `When there is a user registered with phone, it should return that user`() = coroutineScope.runTest {
        val service = createService()
        val mockUser = mockUser(encryptedPhoneNumberDtoFixture).toUser(phoneNumberCrypto)
        val user = service.getOrCreateUser(phoneNumberFixture)
        assertEquals(mockUser, user)
    }

    @Test
    fun `When there is no user registered with phone, it should create and return that user`() =
        coroutineScope.runTest {
            val phoneDto = encryptedPhoneNumberDtoFixture
            Mockito.`when`(userRepository.findUserByPhone(phone = phoneDto)) doReturn null
            val newUser = phoneDto.toUser()
            Mockito.`when`(userRepository.save(any())) doReturn newUser

            val service = createService()
            val user = service.getOrCreateUser(phoneNumberFixture)
            assertEquals(newUser.toUser(phoneNumberCrypto), user)
        }

    private fun mockUser(phoneDto: EncryptedPhoneNumberDto): UserDto {
        val userDto = phoneDto.toUser()
        Mockito.`when`(userRepository.findUserByPhone(phone = phoneDto)) doReturn userDto
        return userDto
    }

    private fun EncryptedPhoneNumberDto.toUser(): UserDto =
        UserDto(phone = this, createdAt = timeInstant.now(), updatedAt = timeInstant.now(), name = "")

}