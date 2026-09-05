package com.nodephone.android.feature.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nodephone.android.core.`project-runtime`.ProjectRuntimeManager
import com.nodephone.android.data.projects.ProjectRepository
import com.nodephone.android.domain.projects.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val runtimeManager: ProjectRuntimeManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isCreateDialogOpen = MutableStateFlow(false)
    private val _projectToDuplicate = MutableStateFlow<Project?>(null)
    private val _projectToDelete = MutableStateFlow<Project?>(null)

    val uiState: StateFlow<ProjectsUiState> = combine(
        repository.projects,
        repository.activeProject,
        _searchQuery,
        _isCreateDialogOpen,
        _projectToDuplicate,
        _projectToDelete
    ) { projects, active, query, isCreateOpen, toDup, toDel ->
        val filtered = if (query.isBlank()) {
            projects
        } else {
            projects.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.id.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        ProjectsUiState(
            projects = filtered,
            activeProject = active,
            searchQuery = query,
            isCreateDialogOpen = isCreateOpen,
            projectToDuplicate = toDup,
            projectToDelete = toDel
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProjectsUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openCreateDialog() {
        _isCreateDialogOpen.value = true
    }

    fun closeCreateDialog() {
        _isCreateDialogOpen.value = false
    }

    fun createProject(
        name: String,
        description: String,
        databaseName: String,
        auth: Boolean,
        storage: Boolean,
        realtime: Boolean,
        seed: Boolean
    ) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                val id = name.trim().lowercase().replace("\\s+".toRegex(), "-")
                runtimeManager.createProject(
                    id = id,
                    name = name.trim(),
                    description = description.trim(),
                    databaseName = databaseName.trim(),
                    authEnabled = auth,
                    storageEnabled = storage,
                    realtimeEnabled = realtime,
                    seedSampleData = seed
                )
            }
            _isCreateDialogOpen.value = false
        }
    }

    fun switchActiveProject(projectId: String) {
        viewModelScope.launch {
            runtimeManager.switchActiveProject(projectId)
        }
    }

    fun openDuplicateDialog(project: Project) {
        _projectToDuplicate.value = project
    }

    fun closeDuplicateDialog() {
        _projectToDuplicate.value = null
    }

    fun confirmDuplicate(sourceId: String, newName: String) {
        viewModelScope.launch {
            runtimeManager.duplicateProject(sourceId, newName)
            _projectToDuplicate.value = null
        }
    }

    fun openDeleteDialog(project: Project) {
        _projectToDelete.value = project
    }

    fun closeDeleteDialog() {
        _projectToDelete.value = null
    }

    fun confirmDelete(projectId: String) {
        viewModelScope.launch {
            runtimeManager.deleteProject(projectId)
            _projectToDelete.value = null
        }
    }
}
