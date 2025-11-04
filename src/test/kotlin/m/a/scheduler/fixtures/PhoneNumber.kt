package m.a.scheduler.fixtures

import m.a.scheduler.auth.database.model.EncryptedPhoneNumberDto
import m.a.scheduler.auth.model.PhoneNumber


internal val phoneNumberFixture = PhoneNumber("9192493674", PhoneNumber.CountryCode.Iran)
internal val encryptedPhoneNumberDtoFixture = EncryptedPhoneNumberDto(
    "EncryptedPhoneNumber", "Iran"
)