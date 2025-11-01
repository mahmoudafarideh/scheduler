package m.a.scheduler.user.service

import kotlinx.coroutines.withContext
import m.a.scheduler.app.base.CoroutineDispatcherProvider
import m.a.scheduler.app.base.TimeInstant
import m.a.scheduler.auth.database.model.PhoneNumberDto
import m.a.scheduler.auth.database.model.toPhoneNumberDto
import m.a.scheduler.auth.model.PhoneNumber
import m.a.scheduler.auth.service.GetAuthUserId
import m.a.scheduler.user.database.model.UserDto
import m.a.scheduler.user.database.model.toUser
import m.a.scheduler.user.database.repository.UserRepository
import m.a.scheduler.user.model.User
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val userRepository: UserRepository,
    private val timeInstant: TimeInstant,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val getAuthUserId: GetAuthUserId
) {
    suspend fun getOrCreateUser(phoneNumber: PhoneNumber): User {
        return withContext(coroutineDispatcherProvider.io) {
            val phoneNumberDto = phoneNumber.toPhoneNumberDto()
            val user = userRepository.findUserByPhone(phoneNumberDto)
                ?: userRepository.save(createUserDto(phoneNumberDto))
            user.toUser()
        }
    }

    suspend fun getUserById(id: String): User? {
        return userRepository.findByIdOrNull(ObjectId(id))?.toUser()
    }

    suspend fun getAuthUser(): User? {
        val userId = getAuthUserId.execute()
        return withContext(coroutineDispatcherProvider.io) {
            userRepository.findById(ObjectId(userId)).getOrNull()?.toUser()
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