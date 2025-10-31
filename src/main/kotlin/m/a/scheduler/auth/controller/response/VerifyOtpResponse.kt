package m.a.scheduler.auth.controller.response

import m.a.scheduler.app.exception.InvalidArgumentException
import m.a.scheduler.auth.model.VerifyOtpResult

internal fun VerifyOtpResult.Error.toInvalidArgumentException(): InvalidArgumentException {
    return when (this) {
        VerifyOtpResult.Error.ExpiredOtp -> InvalidArgumentException("otp", "کد وارد شده منقضی شده است!")
        VerifyOtpResult.Error.InvalidOtp, VerifyOtpResult.Error.NotFound ->
            InvalidArgumentException("otp", "کد وارد شده اشتباه است!")
    }
}