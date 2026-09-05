package com.nodephone.android.feature.trusted

import com.nodephone.android.domain.trusted.model.TrustedDevice

data class TrustedDevicesUiState(
    val devices: List<TrustedDevice> = emptyList(),
    val isLoading: Boolean = false,
    val deviceToRename: TrustedDevice? = null
)
