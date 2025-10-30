package m.a.scheduler.app.config

import m.a.scheduler.app.security.PublicApi
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*


@Component
class PublicApiScanner(
    private val handlerMapping: RequestMappingHandlerMapping?
) {
    fun getPermitAllPaths(): List<String> {
        return handlerMapping!!.handlerMethods.entries.stream()
            .filter { entry: Map.Entry<RequestMappingInfo?, HandlerMethod> ->
                return@filter entry.value.getMethodAnnotation(PublicApi::class.java) != null
            }
            .flatMap { entry: Map.Entry<RequestMappingInfo, HandlerMethod?> ->
                entry.key.patternValues.stream()
            }.filter(Objects::nonNull).toList()
    }
}