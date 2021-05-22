package com.example.scanner.di

import com.example.scanner.api.ScannerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideScannerService() : ScannerService {
        return ScannerService.create()
    }
}