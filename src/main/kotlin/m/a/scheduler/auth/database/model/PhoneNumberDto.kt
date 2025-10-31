package m.a.scheduler.auth.database.model

import m.a.scheduler.auth.model.PhoneNumber

data class PhoneNumberDto(
    val phoneNumber: String,
    val countryCode: String,
)

fun PhoneNumberDto.toPhoneNumber(): PhoneNumber {
    return PhoneNumber(
        this.phoneNumber,
        PhoneNumber.CountryCode.entries.first { it.countryCode == this.countryCode }
    )
}

fun PhoneNumber.toPhoneNumberDto() = PhoneNumberDto(this.number, this.countryCode.countryCode)