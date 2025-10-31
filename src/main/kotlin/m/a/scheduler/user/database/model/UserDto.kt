package m.a.scheduler.user.database.model

import m.a.scheduler.auth.database.model.PhoneNumberDto
import m.a.scheduler.auth.database.model.toPhoneNumber
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
            unique = false
        )
    ]
)
data class UserDto(
    @Id val id: ObjectId = ObjectId.get(),
    val phone: PhoneNumberDto,
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

internal fun UserDto.toUser() = User(
    id = id.toHexString(),
    phone = phone.toPhoneNumber(),
    name = name,
    state = when (state) {
        UserDto.State.Active -> User.State.Active
        UserDto.State.Deactivated -> User.State.Deactivated
        UserDto.State.Banned -> User.State.Banned
    }
)