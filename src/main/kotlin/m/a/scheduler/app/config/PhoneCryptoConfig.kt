package m.a.scheduler.app.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("phone.crypto.config")
class PhoneCryptoConfig {
    var secretKey: String = ""
}