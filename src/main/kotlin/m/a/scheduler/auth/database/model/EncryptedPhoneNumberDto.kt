package m.a.scheduler.auth.database.model

data class EncryptedPhoneNumberDto(
    val phoneNumber: String,
    val countryCode: String,
)
