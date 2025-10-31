package m.a.scheduler.app.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("sms.config")
class SmsConfig {
    var accessKey: String = ""
    var otpCodePattern: String = ""
}