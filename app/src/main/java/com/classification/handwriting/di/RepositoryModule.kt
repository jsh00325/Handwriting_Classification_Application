package com.classification.handwriting.di

import com.classification.handwriting.data.repositoryImpl.ModelRepositoryImpl
import com.classification.handwriting.domain.repository.ModelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindModelRepository(
        modelRepository: ModelRepositoryImpl
    ): ModelRepository
}