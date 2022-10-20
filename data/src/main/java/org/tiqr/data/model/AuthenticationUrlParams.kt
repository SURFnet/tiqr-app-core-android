package org.tiqr.data.model

import android.net.Uri
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import timber.log.Timber

class AuthenticationUrlParams private constructor(
    val username: String?,
    val protocolVersion: Int,
    val sessionKey: String,
    val challenge: String,
    val serverIdentifier: String,
    val returnUrl: String?
) {
    companion object {
        /**
         * Tries to parse the challenge authentication URL params from a given URL.
         * If the parse could not complete because of missing information, null will be returned.
         */
        fun parseFromUrl(url: String): AuthenticationUrlParams? {
            return if (url.startsWith("http")) {
                // New format URL
                parseNewFormatUrl(url)
            } else if (url.startsWith("${TiqrConfig.authScheme}://")) {
                // Old format URL
                parseOldFormatUrl(url)
            } else {
                Timber.i("Could not parse URL for authentication challenge, URL is: '$url'")
                null
            }
        }

        private fun parseOldFormatUrl(url: String): AuthenticationUrlParams? {
            val httpUrl =
                url.replaceFirst("${TiqrConfig.authScheme}://", "http://").toHttpUrlOrNull()
            if (httpUrl == null) {
                Timber.w("Could not parse old format URL.")
                return null
            }
            if (!TiqrConfig.enforceChallengeHosts.isNullOrBlank()) {
                val host = httpUrl.host.lowercase()
                var matchesHost = false
                TiqrConfig.enforceChallengeHosts!!.split(",").forEach { enforcedHost ->
                    if (host == enforcedHost || host.endsWith(".$enforcedHost")) {
                        matchesHost = true
                    }
                }
                if (!matchesHost) {
                    Timber.w("Host was expected to be a subdomain of: ${TiqrConfig.enforceChallengeHosts}, but it was actually: $host.");
                    return null
                }
            }
            val username = httpUrl.username
            val protocolVersion = httpUrl.pathSegments.getOrNull(3)?.toInt() ?: 0
            val sessionKey = httpUrl.pathSegments[0]
            val challenge = httpUrl.pathSegments[1]
            val returnUrl = httpUrl.query?.toHttpUrlOrNull()?.toString()
            val serverIdentifier = httpUrl.host
            return AuthenticationUrlParams(
                username = username,
                protocolVersion = protocolVersion,
                sessionKey = sessionKey,
                challenge = challenge,
                returnUrl = returnUrl,
                serverIdentifier = serverIdentifier
            )
        }

        private fun parseNewFormatUrl(url: String): AuthenticationUrlParams? {
            val uri = try {
                Uri.parse(url)
            } catch (ex: Exception) {
                Timber.w(ex, "Unable to parse URL: '$url'")
                return null
            }
            if (!TiqrConfig.enforceChallengeHosts.isNullOrBlank()) {
                val host = uri.host?.lowercase()
                var matchesHost = false
                TiqrConfig.enforceChallengeHosts!!.split(",").forEach { enforcedHost ->
                    if (host != null && (host == enforcedHost || host.endsWith(".$enforcedHost"))) {
                        matchesHost = true
                    }
                }
                if (!matchesHost) {
                    Timber.w("Host was expected to be a subdomain of: ${TiqrConfig.enforceChallengeHosts}, but it was actually: $host.");
                    return null
                }
            }
            val firstPathParam = uri.pathSegments.firstOrNull()
            if (firstPathParam == null) {
                Timber.w("Expected a path parameter, got nothing, this is not a valid challenge URL.")
                return null
            }
            if (firstPathParam.lowercase() != TiqrConfig.authPathParam?.lowercase()) {
                Timber.w("Challenge URL not according to format. Expected path parameter: '${TiqrConfig.authPathParam}', got: '$firstPathParam'.")
                return null
            }
            val username = uri.getQueryParameter("u")
            val protocolVersion = uri.getQueryParameter("v")?.toIntOrNull() ?: 0
            val challenge = uri.getQueryParameter("q") ?: return missingParameter("q")
            val sessionKey = uri.getQueryParameter("s") ?: return missingParameter("s")
            val serverIdentifier = uri.getQueryParameter("i") ?: return missingParameter("i")
            val returnUrl: String? = null

            return AuthenticationUrlParams(
                username = username,
                protocolVersion = protocolVersion,
                sessionKey = sessionKey,
                challenge = challenge,
                returnUrl = returnUrl,
                serverIdentifier = serverIdentifier
            )
        }

        private fun missingParameter(paramName: String): AuthenticationUrlParams? {
            Timber.w("Expected the following parameter: '$paramName'!")
            return null
        }
    }
}
