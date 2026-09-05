package com.nodephone.android.di

import android.content.Context
import androidx.room.Room
import com.nodephone.android.core.server.NodePhoneServerEngine
import com.nodephone.android.core.server.NodePhoneServerManager
import com.nodephone.android.data.local.NodePhoneDatabase
import com.nodephone.android.data.local.dao.ServerConfigDao
import com.nodephone.android.data.pairing.PairingRepository
import com.nodephone.android.data.pairing.PairingRepositoryImpl
import com.nodephone.android.data.repository.ServerRepository
import com.nodephone.android.data.repository.ServerRepositoryImpl
import com.nodephone.android.data.repository.SettingsRepository
import com.nodephone.android.data.repository.SettingsRepositoryImpl
import com.nodephone.android.data.trusted.TrustedDeviceRepository
import com.nodephone.android.data.trusted.TrustedDeviceRepositoryImpl
import com.nodephone.android.data.trusted.dao.TrustedDeviceDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindPairingRepository(
        impl: PairingRepositoryImpl
    ): PairingRepository

    @Binds
    @Singleton
    abstract fun bindTrustedDeviceRepository(
        impl: TrustedDeviceRepositoryImpl
    ): TrustedDeviceRepository

    companion object {
        @Provides
        @Singleton
        fun provideNodePhoneServerManager(
            engine: NodePhoneServerEngine
        ): NodePhoneServerManager {
            return NodePhoneServerManager(engine)
        }

        @Provides
        @Singleton
        fun provideNodePhoneDatabase(
            @ApplicationContext context: Context
        ): NodePhoneDatabase {
            return Room.databaseBuilder(
                context,
                NodePhoneDatabase::class.java,
                "nodephone.db"
            ).fallbackToDestructiveMigration().build()
        }

        @Provides
        @Singleton
        fun provideServerConfigDao(
            db: NodePhoneDatabase
        ): ServerConfigDao {
            return db.serverConfigDao()
        }

        @Provides
        @Singleton
        fun provideTrustedDeviceDao(
            db: NodePhoneDatabase
        ): TrustedDeviceDao {
            return db.trustedDeviceDao()
        }
    }
}
