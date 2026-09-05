package com.nodephone.android.feature.projects

import com.nodephone.android.domain.projects.model.Project

data class ProjectsUiState(
    val projects: List<Project> = emptyList(),
    val activeProject: Project? = null,
    val searchQuery: String = "",
    val isCreateDialogOpen: Boolean = false,
    val projectToDuplicate: Project? = null,
    val projectToDelete: Project? = null
)
