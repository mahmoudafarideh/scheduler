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
            name = "phone_number",
            def = "{ 'phone': 1 }",
            unique = false
        )
    ]
)
data class PhoneNumberOtpDto(
    @Id
    val id: ObjectId = ObjectId.get(),
    val phone: EncryptedPhoneNumberDto,
    val otp: String,
    val createdAt: Date,
    val updatedAt: Date,
    val expiresAt: Date,
    val status: Status = Status.Pending
) {
    enum class Status {
        Pending,
        Revoked,
        Consumed
    }
}
