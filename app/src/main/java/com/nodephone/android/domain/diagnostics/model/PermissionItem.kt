package com.nodephone.android.domain.diagnostics.model

data class PermissionItem(
    val name: String,
    val description: String,
    val permissionKey: String,
    val isGranted: Boolean,
    val isRequired: Boolean = true
)
