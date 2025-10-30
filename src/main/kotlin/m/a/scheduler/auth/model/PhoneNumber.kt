package m.a.scheduler.auth.model

data class PhoneNumber(
    val number: String,
    val countryCode: CountryCode = CountryCode.Iran,
) {
    enum class CountryCode(val countryCode: String) {
        Iran("98")
    }
}
