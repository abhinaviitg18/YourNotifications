package com.yournotifications.messaging

import android.util.Base64
import com.yournotifications.data.db.WebhookLogEntity
import com.yournotifications.data.db.WebhookSettingsDao
import com.yournotifications.data.model.BucketType
import com.yournotifications.data.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebhookSender @Inject constructor(
    private val webhookSettingsDao: WebhookSettingsDao,
    private val repository: NotificationRepository
) {
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun send(id: Long, packageName: String, appName: String, title: String?, text: String?, bucket: BucketType, timestamp: Long) {
        scope.launch {
            val sentAt = System.currentTimeMillis()
            val body = if (!title.isNullOrBlank()) "$title: $text" else text ?: ""
            
            val json = JSONObject().apply {
                put("packageName", packageName)
                put("sender", appName)
                put("body", body)
                put("bucket", bucket.name)
                put("timestamp", timestamp)
            }
            val jsonStr = json.toString()

            val app = repository.getApp(packageName)
            if (app?.isWebhookEnabled == false) {
                return@launch
            }

            val settings = webhookSettingsDao.getSettings()
            if (settings == null || !settings.isEnabled || settings.url.isBlank()) {
                // Log skip if settings exist but disabled, or just return if no settings
                if (settings != null) {
                    repository.insertWebhookLog(
                        WebhookLogEntity(
                            url = settings.url,
                            requestBody = jsonStr,
                            httpStatusCode = null,
                            responseBody = null,
                            isSuccess = false,
                            errorMessage = if (settings.url.isBlank()) "URL is blank" else "Webhook disabled",
                            sentAt = sentAt
                        )
                    )
                }
                return@launch
            }

            // Bucket filtering
            if (bucket !in settings.targetBuckets) {
                repository.insertWebhookLog(
                    WebhookLogEntity(
                        url = settings.url,
                        requestBody = jsonStr,
                        httpStatusCode = null,
                        responseBody = null,
                        isSuccess = false,
                        errorMessage = "Skipped: Bucket ${bucket.name} not in target list",
                        sentAt = sentAt
                    )
                )
                return@launch
            }

            val requestBuilder = Request.Builder()
                .url(settings.url)
                .post(jsonStr.toRequestBody("application/json".toMediaType()))

            if (settings.signingSecret.isNotBlank()) {
                try {
                    val mac = Mac.getInstance("HmacSHA256")
                    mac.init(SecretKeySpec(settings.signingSecret.toByteArray(), "HmacSHA256"))
                    val sig = Base64.encodeToString(mac.doFinal(jsonStr.toByteArray()), Base64.NO_WRAP)
                    requestBuilder.addHeader("X-Webhook-Signature", "sha256=$sig")
                } catch (_: Exception) {}
            }

            try {
                client.newCall(requestBuilder.build()).execute().use { response ->
                    val success = response.isSuccessful
                    repository.insertWebhookLog(
                        WebhookLogEntity(
                            url = settings.url,
                            requestBody = jsonStr,
                            httpStatusCode = response.code,
                            responseBody = response.body?.string()?.take(500),
                            isSuccess = success,
                            errorMessage = if (!success) "HTTP ${response.code}" else null,
                            sentAt = sentAt
                        )
                    )
                    if (success) {
                        repository.updateWebhookStatus(id, true)
                    }
                }
            } catch (e: Exception) {
                repository.insertWebhookLog(
                    WebhookLogEntity(
                        url = settings.url,
                        requestBody = jsonStr,
                        httpStatusCode = null,
                        responseBody = null,
                        isSuccess = false,
                        errorMessage = e.message,
                        sentAt = sentAt
                    )
                )
            }
        }
    }
}
