package com.nodephone.android.core.`project-runtime`

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val projectsBaseDir: File by lazy {
        File(context.filesDir, "NodePhone/projects").apply {
            if (!exists()) mkdirs()
        }
    }

    fun getProjectDir(projectId: String): File {
        return File(projectsBaseDir, projectId).apply {
            if (!exists()) mkdirs()
        }
    }

    fun initializeProjectDirectories(projectId: String) {
        val base = getProjectDir(projectId)
        File(base, "database").mkdirs()
        File(base, "storage").mkdirs()
        File(base, "functions").mkdirs()
        File(base, "logs").mkdirs()
        File(base, "config").mkdirs()
    }

    fun duplicateProjectFiles(sourceProjectId: String, targetProjectId: String) {
        val sourceDir = getProjectDir(sourceProjectId)
        val targetDir = getProjectDir(targetProjectId)
        if (sourceDir.exists()) {
            copyDirectory(sourceDir, targetDir)
        }
    }

    fun deleteProjectFiles(projectId: String) {
        val dir = getProjectDir(projectId)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }

    fun getProjectMetricsBytes(projectId: String): Pair<Long, Long> {
        val base = getProjectDir(projectId)
        val dbDir = File(base, "database")
        val storageDir = File(base, "storage")
        return Pair(calculateDirSize(dbDir), calculateDirSize(storageDir))
    }

    private fun copyDirectory(source: File, target: File) {
        if (source.isDirectory) {
            if (!target.exists()) target.mkdirs()
            val children = source.list() ?: return
            for (child in children) {
                copyDirectory(File(source, child), File(target, child))
            }
        } else {
            source.copyTo(target, overwrite = true)
        }
    }

    private fun calculateDirSize(dir: File): Long {
        if (!dir.exists()) return 0L
        var size = 0L
        val files = dir.listFiles() ?: return 0L
        for (f in files) {
            size += if (f.isFile) f.length() else calculateDirSize(f)
        }
        return size
    }
}
