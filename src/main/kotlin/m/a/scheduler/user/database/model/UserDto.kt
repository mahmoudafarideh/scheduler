package m.a.scheduler.user.database.model

import m.a.scheduler.auth.database.model.EncryptedPhoneNumberDto
import m.a.scheduler.auth.database.utils.PhoneNumberCrypto
import m.a.scheduler.user.model.User
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "users")
@CompoundIndexes(
    value = [
        CompoundIndex(
            name = "phone_number",
            def = "{ 'phone': 1 }",
            unique = true
        )
    ]
)
data class UserDto(
    @Id val id: ObjectId = ObjectId.get(),
    val phone: EncryptedPhoneNumberDto,
    val name: String,
    val state: State = State.Active,
    val createdAt: Date,
    val updatedAt: Date,
) {
    enum class State {
        Active,
        Deactivated,
        Banned
    }
}

fun UserDto.toUser(phoneNumberCrypto: PhoneNumberCrypto) = User(
    id = id.toHexString(),
    phone = phoneNumberCrypto.decrypt(phone),
    name = name,
    state = when (state) {
        UserDto.State.Active -> User.State.Active
        UserDto.State.Deactivated -> User.State.Deactivated
        UserDto.State.Banned -> User.State.Banned
    }
)