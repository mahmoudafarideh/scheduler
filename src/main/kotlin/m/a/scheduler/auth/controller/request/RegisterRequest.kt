package m.a.scheduler.auth.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import m.a.scheduler.auth.model.PhoneNumber

data class RegisterRequest(
    @field:Pattern(
        regexp = "^09[0-9]{9}$",
        message = "یک شماره موبایل معتبر وارد کنید!",
    )
    @field:NotBlank
    val phoneNumber: String,
)

internal fun RegisterRequest.toPhoneNumber(): PhoneNumber = PhoneNumber(
    phoneNumber.replaceFirst("0", ""),
    PhoneNumber.CountryCode.Iran
)
