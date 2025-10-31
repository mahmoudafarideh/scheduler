package m.a.scheduler.auth.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import m.a.scheduler.auth.model.PhoneNumber

data class VerifyOtpRequest(
    @field:Pattern(
        regexp = "^09[0-9]{9}$",
        message = "یک شماره موبایل معتبر وارد کنید!",
    )
    @field:NotBlank
    val phone: String,
    @field:Pattern(
        regexp = "^[0-9]{6}$",
        message = "کد ۶ رقمی صحیح وارد کنید!",
    )
    @field:NotBlank
    val otp: String,
)


internal fun VerifyOtpRequest.toPhoneNumber(): PhoneNumber = PhoneNumber(
    phone.replaceFirst("0", ""),
    PhoneNumber.CountryCode.Iran
)
