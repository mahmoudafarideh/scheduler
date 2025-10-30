package m.a.scheduler.app.base

import org.springframework.stereotype.Component
import java.util.*

@Component
class TimeInstant {
    fun now() = Date()
}