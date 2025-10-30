package m.a.scheduler.auth.controller

import m.a.scheduler.auth.controller.request.RegisterRequest
import jakarta.validation.Valid
import m.a.scheduler.auth.security.PublicApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {
    @PublicApi
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): String {
        return request.phoneNumber
    }
}