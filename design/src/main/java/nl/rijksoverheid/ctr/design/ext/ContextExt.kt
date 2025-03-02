package nl.rijksoverheid.ctr.design.ext

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.accessibility.AccessibilityManager
import nl.rijksoverheid.ctr.design.R

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
fun Context.getAppThemeWindowBackgroundColor(): Int {
    val theme = ContextThemeWrapper(this, R.style.AppTheme).theme
    val typedValue = TypedValue()
    theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
    return typedValue.data
}

fun Context.isScreenReaderOn(): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    if (am != null && am.isEnabled) {
        val serviceInfoList =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
        if (serviceInfoList.isNotEmpty())
            return true
    }
    return false
}
