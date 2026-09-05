package com.nodephone.android.core.`project-runtime`

import com.nodephone.android.data.projects.ProjectRepository
import com.nodephone.android.data.repository.ServerRepository
import com.nodephone.android.domain.projects.model.Project
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRuntimeManager @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val projectStorageManager: ProjectStorageManager,
    private val serverRepository: ServerRepository
) {
    suspend fun createProject(
        id: String,
        name: String,
        description: String,
        databaseName: String,
        authEnabled: Boolean,
        storageEnabled: Boolean,
        realtimeEnabled: Boolean,
        seedSampleData: Boolean
    ): Project {
        val cleanId = id.trim().lowercase().replace("\\s+".toRegex(), "-")
        projectStorageManager.initializeProjectDirectories(cleanId)

        val project = Project(
            id = cleanId,
            name = name,
            description = description,
            databaseName = if (databaseName.isBlank()) "nodephone.sqlite" else databaseName,
            authEnabled = authEnabled,
            storageEnabled = storageEnabled,
            realtimeEnabled = realtimeEnabled,
            isActive = true
        )

        projectRepository.addOrUpdateProject(project)
        projectRepository.setActiveProject(cleanId)
        serverRepository.restartServer()
        return project
    }

    suspend fun switchActiveProject(projectId: String) {
        val project = projectRepository.getProjectById(projectId) ?: return
        projectStorageManager.initializeProjectDirectories(projectId)
        projectRepository.setActiveProject(projectId)
        serverRepository.restartServer()
    }

    suspend fun duplicateProject(sourceProjectId: String, newName: String): Project? {
        val source = projectRepository.getProjectById(sourceProjectId) ?: return null
        val newId = "${source.id}-copy-${System.currentTimeMillis() % 10000}"

        projectStorageManager.duplicateProjectFiles(sourceProjectId, newId)

        val copy = source.copy(
            id = newId,
            name = if (newName.isBlank()) "${source.name} (Copy)" else newName,
            isActive = false,
            createdTimestamp = System.currentTimeMillis(),
            lastOpenedTimestamp = System.currentTimeMillis()
        )

        projectRepository.addOrUpdateProject(copy)
        return copy
    }

    suspend fun deleteProject(projectId: String) {
        projectStorageManager.deleteProjectFiles(projectId)
        projectRepository.deleteProject(projectId)
    }
}
