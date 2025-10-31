package m.a.scheduler.auth.database.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("refresh_token")
@CompoundIndexes(
    value = [
        CompoundIndex(
            name = "user_token",
            def = "{ 'hashedToken': 1, 'userId': 1 }",
            unique = false
        )
    ]
)
data class RefreshTokenDto(
    val userId: ObjectId,
    val hashedToken: String,
    @Indexed(expireAfter = "0s")
    val expiresAt: Date,
    val createdAt: Date,
)