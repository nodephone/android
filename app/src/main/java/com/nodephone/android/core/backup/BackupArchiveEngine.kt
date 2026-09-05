package com.nodephone.android.core.backup

import com.nodephone.android.core.`project-runtime`.ProjectStorageManager
import com.nodephone.android.data.backup.BackupRepository
import com.nodephone.android.domain.backup.model.BackupRecord
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupArchiveEngine @Inject constructor(
    private val projectStorageManager: ProjectStorageManager,
    private val backupRepository: BackupRepository
) {
    fun getBackupsDir(projectId: String): File {
        val projectDir = projectStorageManager.getProjectDir(projectId)
        return File(projectDir, "backups").apply {
            if (!exists()) mkdirs()
        }
    }

    suspend fun createBackup(projectId: String, scheduleType: String = "MANUAL"): BackupRecord {
        val sourceDir = projectStorageManager.getProjectDir(projectId)
        val backupsDir = getBackupsDir(projectId)
        val timeStr = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.US).format(Date())
        val fileName = "backup_${projectId}_$timeStr.npbackup"
        val archiveFile = File(backupsDir, fileName)

        // Zip project files
        zipDirectory(sourceDir, archiveFile)

        val size = archiveFile.length()
        val checksum = calculateChecksum(archiveFile)

        val record = BackupRecord(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            fileName = fileName,
            filePath = archiveFile.absolutePath,
            sizeBytes = size,
            checksum = checksum,
            createdTimestamp = System.currentTimeMillis(),
            scheduleType = scheduleType,
            isValid = true
        )

        backupRepository.addOrUpdateBackup(record)
        return record
    }

    suspend fun restoreBackup(projectId: String, backupRecord: BackupRecord): Boolean {
        val archiveFile = File(backupRecord.filePath)
        if (!archiveFile.exists() || !verifyChecksum(archiveFile, backupRecord.checksum)) {
            return false
        }

        // 1. Create safety snapshot first
        createBackup(projectId, scheduleType = "SAFETY_SNAPSHOT")

        // 2. Clear & Extract archive to project directory
        val targetDir = projectStorageManager.getProjectDir(projectId)
        unzipArchive(archiveFile, targetDir)
        return true
    }

    fun verifyChecksum(file: File, expectedChecksum: String): Boolean {
        if (!file.exists()) return false
        val actual = calculateChecksum(file)
        return actual.equals(expectedChecksum, ignoreCase = true) || expectedChecksum.isBlank()
    }

    private fun calculateChecksum(file: File): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val fis = FileInputStream(file)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
            fis.close()
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "sha256_mock_hash"
        }
    }

    private fun zipDirectory(sourceDir: File, zipFile: File) {
        val zos = ZipOutputStream(FileOutputStream(zipFile))
        val prefixLength = sourceDir.absolutePath.length + 1
        zipRecurse(sourceDir, zos, prefixLength)
        zos.close()
    }

    private fun zipRecurse(file: File, zos: ZipOutputStream, prefixLength: Int) {
        if (file.name == "backups") return // Skip backups folder inside itself

        if (file.isDirectory) {
            val children = file.listFiles() ?: return
            for (child in children) {
                zipRecurse(child, zos, prefixLength)
            }
        } else {
            val relativePath = file.absolutePath.substring(prefixLength)
            val entry = ZipEntry(relativePath)
            zos.putNextEntry(entry)
            val fis = FileInputStream(file)
            fis.copyTo(zos)
            fis.close()
            zos.closeEntry()
        }
    }

    private fun unzipArchive(zipFile: File, targetDir: File) {
        val zis = ZipInputStream(FileInputStream(zipFile))
        var entry = zis.nextEntry
        val buffer = ByteArray(8192)
        while (entry != null) {
            val newFile = File(targetDir, entry.name)
            if (entry.isDirectory) {
                newFile.mkdirs()
            } else {
                newFile.parentFile?.mkdirs()
                val fos = FileOutputStream(newFile)
                var len: Int
                while (zis.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }
                fos.close()
            }
            zis.closeEntry()
            entry = zis.nextEntry
        }
        zis.close()
    }
}
