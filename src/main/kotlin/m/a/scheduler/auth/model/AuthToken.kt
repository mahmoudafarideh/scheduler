package m.a.scheduler.auth.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)
