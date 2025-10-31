package m.a.scheduler.app.base

import kotlinx.coroutines.*

abstract class BackgroundTask<T> {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(job + Dispatchers.IO)

    fun schedule(
        data: T,
        initialDelay: Long = 0,
        repeatOnFailure: Int = 0,
    ) {
        coroutineScope.launch {
            delay(initialDelay)
            if (repeatOnFailure == 0) {
                kotlin.runCatching {
                    execute(data)
                }
            } else {
                repeat(repeatOnFailure) {
                    execute(data)
                }
            }
        }
    }

    protected abstract suspend fun execute(data: T)
}