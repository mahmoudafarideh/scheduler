package m.a.scheduler.auth.database.utils

import org.springframework.stereotype.Component
import java.util.*

@Component
class OtpCodeGenerator {
    fun generateOtpCode(length: Int = 6): String {
        return UUID.randomUUID().toString().take(length)
    }
}