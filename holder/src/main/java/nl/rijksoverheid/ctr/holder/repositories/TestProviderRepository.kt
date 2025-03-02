package nl.rijksoverheid.ctr.holder.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.rijksoverheid.ctr.api.interceptors.SigningCertificate
import nl.rijksoverheid.ctr.holder.api.TestProviderApiClient
import nl.rijksoverheid.ctr.holder.models.RemoteTestResult
import nl.rijksoverheid.ctr.holder.models.SignedResponseWithModel
import nl.rijksoverheid.ctr.holder.models.post.GetTestResultPostData
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
interface TestProviderRepository {
    suspend fun remoteTestResult(
        url: String,
        token: String,
        verifierCode: String?,
        signingCertificateBytes: ByteArray
    ): SignedResponseWithModel<RemoteTestResult>
}

class TestProviderRepositoryImpl(
    private val testProviderApiClient: TestProviderApiClient,
    private val responseConverter: Converter<ResponseBody, SignedResponseWithModel<RemoteTestResult>>,
) : TestProviderRepository {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun remoteTestResult(
        url: String,
        token: String,
        verifierCode: String?,
        signingCertificateBytes: ByteArray
    ): SignedResponseWithModel<RemoteTestResult> {
        try {
            return testProviderApiClient.getTestResult(
                url = url,
                authorization = "Bearer $token",
                data = verifierCode?.let {
                    GetTestResultPostData(
                        it
                    )
                },
                certificate = SigningCertificate(signingCertificateBytes),
                protocolVersion = "2.0"
            )
        } catch (ex: HttpException) {
            // if there's no error body, this must be something else than expected
            val errorBody = ex.response()?.errorBody() ?: throw ex
            if (ex.code() == 401) {
                return withContext(Dispatchers.IO) {
                    responseConverter.convert(errorBody) ?: throw ex
                }
            } else {
                throw ex
            }
        }
    }
}
