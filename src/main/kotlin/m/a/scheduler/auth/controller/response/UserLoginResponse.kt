package m.a.scheduler.auth.controller.response

import m.a.scheduler.auth.model.AuthToken
import m.a.scheduler.user.model.User

data class UserLoginResponse(
    val token: TokenResponse,
    val user: UserResponse
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)

data class UserResponse(
    val phoneNumber: String,
    val name: String,
    val id: String
)

internal fun User.toLoginResponse(authToken: AuthToken): UserLoginResponse {
    return UserLoginResponse(
        token = TokenResponse(authToken.accessToken, authToken.refreshToken),
        user = toUserResponse()
    )
}

internal fun User.toUserResponse() = UserResponse(
    name = name,
    phoneNumber = phone.countryCode.countryCode + phone.number,
    id = id
)