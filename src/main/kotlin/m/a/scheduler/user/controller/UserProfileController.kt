package m.a.scheduler.user.controller

import kotlinx.coroutines.runBlocking
import m.a.scheduler.auth.controller.response.UserResponse
import m.a.scheduler.auth.controller.response.toUserResponse
import m.a.scheduler.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class UserProfileController(
    private val userService: UserService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/")
    fun userProfile(): UserResponse? {
        return runBlocking {
            val user = userService.getAuthUser() ?: return@runBlocking null
            user.toUserResponse()
        }
    }
}