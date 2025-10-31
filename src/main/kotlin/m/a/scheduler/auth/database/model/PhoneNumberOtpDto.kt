package m.a.scheduler.auth.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "phone_number_otp")
@CompoundIndexes(
    value = [
        CompoundIndex(
            name = "phone_country_code",
            def = "{ 'phoneNumber': 1, 'countryCode': 1 }",
            unique = false
        )
    ]
)
data class PhoneNumberOtpDto(
    @Id val id: ObjectId = ObjectId.get(),
    val phoneNumber: String,
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
