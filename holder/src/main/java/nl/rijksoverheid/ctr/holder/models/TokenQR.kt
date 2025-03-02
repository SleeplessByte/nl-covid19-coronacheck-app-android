/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.ctr.holder.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenQR (
    val token: String,
    val protocolVersion: String,
    val providerIdentifier: String
)
