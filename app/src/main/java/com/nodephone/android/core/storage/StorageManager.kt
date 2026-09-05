package com.nodephone.android.core.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val rootDir: File by lazy {
        File(context.filesDir, "NodePhone").apply {
            if (!exists()) mkdirs()
        }
    }

    val databaseDir: File by lazy {
        File(rootDir, "database").apply {
            if (!exists()) mkdirs()
        }
    }

    val storageDir: File by lazy {
        File(rootDir, "storage").apply {
            if (!exists()) mkdirs()
        }
    }

    val functionsDir: File by lazy {
        File(rootDir, "functions").apply {
            if (!exists()) mkdirs()
        }
    }

    val backupsDir: File by lazy {
        File(rootDir, "backups").apply {
            if (!exists()) mkdirs()
        }
    }

    val logsDir: File by lazy {
        File(rootDir, "logs").apply {
            if (!exists()) mkdirs()
        }
    }

    init {
        initializeDirectories()
    }

    fun initializeDirectories() {
        databaseDir
        storageDir
        functionsDir
        backupsDir
        logsDir
    }

    fun getDatabaseSizeBytes(): Long {
        return calculateDirectorySize(databaseDir)
    }

    fun getStorageUsageBytes(): Long {
        return calculateDirectorySize(storageDir)
    }

    private fun calculateDirectorySize(directory: File): Long {
        var length: Long = 0
        val files = directory.listFiles() ?: return 0L
        for (file in files) {
            length += if (file.isFile) file.length() else calculateDirectorySize(file)
        }
        return length
    }
}
