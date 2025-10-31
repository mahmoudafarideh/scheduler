package m.a.scheduler.auth.task

import m.a.scheduler.app.base.BackgroundTask
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private const val ippanelUrl = "https://rest.ippanel.com/v1/messages/patterns/send"

@Component
class OtpSendTask(
    @Value("\${sms.config.access-key}")
    private val accessToken: String,
    @Value("\${sms.config.otp-code-pattern}")
    private val otpPatternKey: String,
) : BackgroundTask<OtpTaskInfo>() {

    override suspend fun execute(data: OtpTaskInfo) {
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaType()
        val body = data.createRequestBody(mediaType)
        val request = Request.Builder()
            .url(ippanelUrl)
            .post(body)
            .addHeader("Authorization", "AccessKey $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()
        client.newCall(request).execute()
    }

    private fun OtpTaskInfo.createRequestBody(mediaType: MediaType) = """
                {
        "pattern_code": "$otpPatternKey",
        "originator": "+983000505",
        "recipient": "+${phoneNumber.countryCode.countryCode}${phoneNumber.number}",
        "values": {
            "user": "کاربر",
            "code": "$otp"
        }
    }
            """.trimIndent().toRequestBody(mediaType)
}