package m.a.scheduler.auth.controller.response

data class OtpResponse(
    val resendTimeOutMs: Long
) {
    companion object {
        fun success(timeOutSeconds: Int): OtpResponse = OtpResponse(timeOutSeconds * 1_000L)
    }
}
