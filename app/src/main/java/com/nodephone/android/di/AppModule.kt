package com.nodephone.android.di

import com.nodephone.android.core.server.NodePhoneServerManager
import com.nodephone.android.data.repository.ServerRepository
import com.nodephone.android.data.repository.ServerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindServerRepository(
        impl: ServerRepositoryImpl
    ): ServerRepository

    companion object {
        @Provides
        @Singleton
        fun provideNodePhoneServerManager(): NodePhoneServerManager {
            return NodePhoneServerManager()
        }
    }
}
