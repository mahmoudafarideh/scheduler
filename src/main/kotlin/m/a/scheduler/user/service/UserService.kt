package m.a.scheduler.user.service

import kotlinx.coroutines.withContext
import m.a.scheduler.app.base.CoroutineDispatcherProvider
import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.auth.database.model.PhoneNumberDto
import m.a.scheduler.auth.database.model.toPhoneNumberDto
import m.a.scheduler.auth.model.PhoneNumber
import m.a.scheduler.user.database.model.UserDto
import m.a.scheduler.user.database.model.toUser
import m.a.scheduler.user.database.repository.UserRepository
import m.a.scheduler.user.model.User
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val timeInstant: TimeInstant,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) {
    suspend fun getOrCreateUser(phoneNumber: PhoneNumber): User {
        return withContext(coroutineDispatcherProvider.io) {
            val phoneNumberDto = phoneNumber.toPhoneNumberDto()
            val user = userRepository.findUserByPhone(phoneNumberDto)
                ?: userRepository.save(createUserDto(phoneNumberDto))
            user.toUser()
        }
    }

    private fun createUserDto(phoneNumberDto: PhoneNumberDto) = UserDto(
        phone = phoneNumberDto,
        state = UserDto.State.Active,
        createdAt = timeInstant.now(),
        updatedAt = timeInstant.now(),
        name = ""
    )
}