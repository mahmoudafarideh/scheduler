package m.a.scheduler.auth.controller

import m.a.scheduler.auth.controller.request.RegisterRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {
    @PostMapping("/register")
    fun register(@Valid request: RegisterRequest): String {
        return request.phoneNumber
    }
}