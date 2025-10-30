package m.a.scheduler.auth.database.repository

import m.a.scheduler.auth.database.model.PhoneNumberOtpDto
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PhoneNumberOtpRepository : MongoRepository<PhoneNumberOtpDto, ObjectId> {

    fun findAllByPhoneNumberAndCountryCode(
        phoneNumber: String,
        countryCode: String,
    ): List<PhoneNumberOtpDto>
}