package m.a.scheduler.app.exception

class InvalidArgumentException(
    val objectName: String,
    val errorMessage: String
) : Exception()