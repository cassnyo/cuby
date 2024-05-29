package com.cassnyo.cuby.data.database.di

import android.content.Context
import com.cassnyo.cuby.data.database.CubyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CubyDatabaseModule {

    @Provides
    @Singleton
    fun provideCubyDatabase(@ApplicationContext context: Context): CubyDatabase =
        CubyDatabase.buildDatabase(context)

    @Provides
    fun provideSolveDao(cubyDatabase: CubyDatabase) = cubyDatabase.solveDao()

}