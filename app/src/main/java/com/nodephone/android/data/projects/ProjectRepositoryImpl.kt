package com.nodephone.android.data.projects

import com.nodephone.android.data.projects.dao.ProjectDao
import com.nodephone.android.data.projects.entity.ProjectEntity
import com.nodephone.android.domain.projects.model.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val dao: ProjectDao
) : ProjectRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    override val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _activeProject = MutableStateFlow<Project?>(null)
    override val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    init {
        scope.launch {
            dao.getAllProjectsFlow().collect { list ->
                val domainList = list.map { it.toDomainModel() }
                _projects.value = domainList

                val active = domainList.find { it.isActive }
                if (active == null && domainList.isNotEmpty()) {
                    // Default to first project if none active
                    setActiveProject(domainList.first().id)
                } else {
                    _activeProject.value = active
                }
            }
        }
    }

    override suspend fun getProjectById(id: String): Project? {
        return dao.getProjectById(id)?.toDomainModel()
    }

    override suspend fun addOrUpdateProject(project: Project) {
        dao.insertOrUpdate(project.toEntity())
    }

    override suspend fun setActiveProject(id: String) {
        dao.clearActiveProject()
        dao.setActiveProject(id)
        _activeProject.value = dao.getProjectById(id)?.toDomainModel()
    }

    override suspend fun deleteProject(id: String) {
        dao.deleteProject(id)
        if (_activeProject.value?.id == id) {
            val remaining = dao.getActiveProject()
            _activeProject.value = remaining?.toDomainModel()
        }
    }

    private fun ProjectEntity.toDomainModel(): Project {
        return Project(
            id = id,
            name = name,
            description = description,
            databaseName = databaseName,
            authEnabled = authEnabled,
            storageEnabled = storageEnabled,
            realtimeEnabled = realtimeEnabled,
            isActive = isActive,
            createdTimestamp = createdTimestamp,
            lastOpenedTimestamp = lastOpenedTimestamp,
            databaseSizeBytes = databaseSizeBytes,
            storageSizeBytes = storageSizeBytes
        )
    }

    private fun Project.toEntity(): ProjectEntity {
        return ProjectEntity(
            id = id,
            name = name,
            description = description,
            databaseName = databaseName,
            authEnabled = authEnabled,
            storageEnabled = storageEnabled,
            realtimeEnabled = realtimeEnabled,
            isActive = isActive,
            createdTimestamp = createdTimestamp,
            lastOpenedTimestamp = lastOpenedTimestamp,
            databaseSizeBytes = databaseSizeBytes,
            storageSizeBytes = storageSizeBytes
        )
    }
}
