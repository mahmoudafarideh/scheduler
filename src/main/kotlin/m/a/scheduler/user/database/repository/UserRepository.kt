package m.a.scheduler.user.database.repository

import m.a.scheduler.auth.database.model.PhoneNumberDto
import m.a.scheduler.user.database.model.UserDto
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<UserDto, ObjectId> {
    fun findUserByPhone(phone: PhoneNumberDto): UserDto?
}