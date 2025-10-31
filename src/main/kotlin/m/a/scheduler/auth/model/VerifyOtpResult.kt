package m.a.scheduler.auth.model

sealed class VerifyOtpResult {
    data object Success : VerifyOtpResult()
    sealed class Error : VerifyOtpResult() {
        data object NotFound : Error()
        data object InvalidOtp : Error()
        data object ExpiredOtp : Error()
    }
}