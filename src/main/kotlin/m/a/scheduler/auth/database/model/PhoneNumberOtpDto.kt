package m.a.scheduler.auth.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "phone_number_otp")
data class PhoneNumberOtpDto(
    @Id val id: ObjectId = ObjectId.get(),
    @Indexed val phoneNumber: String,
    val countryCode: String,
    val otp: String,
    val createdAt: Date,
    val updatedAt: Date,
    val expiresAt: Date,
    val status: Status = Status.New
) {
    enum class Status {
        New,
        Sent,
        Revoked,
        Consumed
    }
}
