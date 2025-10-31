package m.a.scheduler.auth.database.repository

import m.a.scheduler.auth.database.model.RefreshTokenDto
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository : MongoRepository<RefreshTokenDto, ObjectId> {
    fun findByUserIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshTokenDto?
    fun deleteByUserIdAndHashedToken(userId: ObjectId, hashedToken: String)
}