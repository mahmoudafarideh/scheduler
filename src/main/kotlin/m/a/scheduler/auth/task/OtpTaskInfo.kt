package m.a.scheduler.auth.task

import m.a.scheduler.auth.model.PhoneNumber

data class OtpTaskInfo(
    val phoneNumber: PhoneNumber,
    val otp: String
)