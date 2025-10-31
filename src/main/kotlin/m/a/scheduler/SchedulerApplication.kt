package m.a.scheduler

import m.a.scheduler.app.config.SmsConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SmsConfig::class)
class SchedulerApplication

fun main(args: Array<String>) {
    runApplication<SchedulerApplication>(*args)
}
