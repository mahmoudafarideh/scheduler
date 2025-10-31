package m.a.scheduler.app.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.ContinuationInterceptor

val CoroutineScope.testDispatcherProvider: CoroutineDispatcherProvider
    get() = object : CoroutineDispatcherProvider {
        private val testDispatcher = coroutineContext[ContinuationInterceptor] as CoroutineDispatcher
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }