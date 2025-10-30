package m.a.scheduler.auth.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class RegisterRequest(
    @field:Pattern(
        regexp = "^09[0-9]{9}$",
        message = "یک شماره موبایل معتبر وارد کنید!",
    )
    @field:NotBlank
    val phoneNumber: String,
)
