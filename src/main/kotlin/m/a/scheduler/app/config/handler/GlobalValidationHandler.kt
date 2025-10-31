package m.a.scheduler.app.config.handler

import m.a.scheduler.app.exception.InvalidArgumentException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalValidationHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = buildMap {
            e.bindingResult.allErrors.forEach {
                set(it.objectName, (it.defaultMessage ?: "Invalid value"))
            }
        }
        return ResponseEntity.status(422).body(mapOf("errors" to errors))
    }

    @ExceptionHandler(InvalidArgumentException::class)
    fun handleValidationError(e: InvalidArgumentException): ResponseEntity<Map<String, Any>> {
        val errors = listOf(mapOf(e.objectName to e.errorMessage))
        return ResponseEntity.status(422).body(mapOf("errors" to errors))
    }
}