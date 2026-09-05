package com.nodephone.android.data.projects

import com.nodephone.android.domain.projects.model.Project
import kotlinx.coroutines.flow.StateFlow

interface ProjectRepository {
    val projects: StateFlow<List<Project>>
    val activeProject: StateFlow<Project?>

    suspend fun getProjectById(id: String): Project?
    suspend fun addOrUpdateProject(project: Project)
    suspend fun setActiveProject(id: String)
    suspend fun deleteProject(id: String)
}
