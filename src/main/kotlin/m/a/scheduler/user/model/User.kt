package m.a.scheduler.user.model

import m.a.scheduler.auth.model.PhoneNumber

data class User(
    val id: String,
    val phone: PhoneNumber,
    val name: String,
    val state: State
) {
    enum class State {
        Active,
        Deactivated,
        Banned
    }
}
