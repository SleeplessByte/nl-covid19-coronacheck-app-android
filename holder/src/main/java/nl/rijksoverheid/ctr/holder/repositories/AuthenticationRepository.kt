package nl.rijksoverheid.ctr.holder.repositories

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import net.openid.appauth.*
import nl.rijksoverheid.ctr.shared.BuildConfig.DIGI_D_BASE_URL
import nl.rijksoverheid.ctr.shared.BuildConfig.DIGI_D_CLIENT_ID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class AuthenticationRepository {

    suspend fun authResponse(
        activityResultLauncher: ActivityResultLauncher<Intent>,
        authService: AuthorizationService
    ) {
        val authServiceConfiguration = authorizationServiceConfiguration()
        val authRequest = authRequest(serviceConfiguration = authServiceConfiguration)
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        activityResultLauncher.launch(authIntent)
    }

    private suspend fun authorizationServiceConfiguration(): AuthorizationServiceConfiguration {
        return suspendCoroutine { continuation ->
            AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(DIGI_D_BASE_URL)) { serviceConfiguration, error ->
                when {
                    error != null -> continuation.resumeWithException(error)
                    serviceConfiguration != null -> continuation.resume(serviceConfiguration)
                    else -> throw Exception("Could not get service configuration")
                }
            }
        }
    }

    private fun authRequest(serviceConfiguration: AuthorizationServiceConfiguration): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            serviceConfiguration,
            DIGI_D_CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse("coronatester-app-dev://auth/login")
        ).build()
    }

    suspend fun accessToken(
        authService: AuthorizationService,
        authResponse: AuthorizationResponse
    ): String {
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(authResponse.createTokenExchangeRequest()) { resp, error ->
                val accessToken = resp?.accessToken
                when {
                    accessToken != null -> continuation.resume(accessToken)
                    error != null -> continuation.resumeWithException(error)
                    else -> continuation.resumeWithException(Exception("Could not get AccessToken"))
                }
            }
        }
    }
}
