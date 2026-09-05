package com.nodephone.android.feature.home

import com.nodephone.android.domain.model.ServerStats
import com.nodephone.android.domain.model.ServerStatus

data class HomeUiState(
    val status: ServerStatus = ServerStatus(),
    val stats: ServerStats = ServerStats(),
    val isLoading: Boolean = false,
    val userMessage: String? = null
)
