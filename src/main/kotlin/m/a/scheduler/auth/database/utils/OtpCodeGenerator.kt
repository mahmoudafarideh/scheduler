package m.a.scheduler.auth.database.utils

import org.springframework.stereotype.Component

@Component
class OtpCodeGenerator {
    fun generateOtpCode(length: Int = 6): String {
        val numbers = ('0'..'9')
        return (1..length)
            .map { numbers.random() }
            .joinToString("")
    }
}