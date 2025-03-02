package nl.rijksoverheid.ctr.verifier.datamappers

import clmobile.Clmobile
import com.squareup.moshi.Moshi
import nl.rijksoverheid.ctr.shared.ext.toObject
import nl.rijksoverheid.ctr.shared.ext.verify
import nl.rijksoverheid.ctr.verifier.models.VerifiedQr

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
interface VerifiedQrDataMapper {
    fun transform(qrContent: String): VerifiedQr
}

class VerifiedQrDataMapperImpl(private val moshi: Moshi) : VerifiedQrDataMapper {
    override fun transform(
        qrContent: String
    ): VerifiedQr {
        val result =
            Clmobile.verifyQREncoded(
                qrContent.toByteArray()
            ).verify()

        return VerifiedQr(
            creationDateSeconds = result.unixTimeSeconds,
            testResultAttributes = result.attributesJson.decodeToString().toObject(moshi)
        )
    }
}
