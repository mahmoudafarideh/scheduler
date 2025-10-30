package m.a.scheduler.app.config

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.util.matcher.RequestMatcher

private const val apiPrefix = "/api"

@Configuration
class SecurityConfig(
    private val publicApiScanner: PublicApiScanner
) {
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        val permitPaths: List<String> = publicApiScanner.getPermitAllPaths()
        return httpSecurity
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(RequestMatcher {
                        it.requestURI.takeIf { path ->
                            path.startsWith(apiPrefix)
                        }?.replaceFirst(apiPrefix, "")?.let { path ->
                            return@let path in permitPaths
                        } ?: false
                    })
                    .permitAll()
                    .dispatcherTypeMatchers(
                        DispatcherType.ERROR,
                        DispatcherType.FORWARD
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .exceptionHandling { configurer ->
                configurer
                    .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .build()
    }
}