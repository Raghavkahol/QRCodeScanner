package com.example.scanner.api

import com.example.scanner.data.SessionInfoRequest
import io.reactivex.Completable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ScannerService {
    @POST("/submit-session")
    fun postSessionInfo(@Body sessionInfoRequest: SessionInfoRequest): Completable

    companion object {
        private const val BASE_URL = "https://en478jh796m7w.x.pipedream.net/"

        fun create(): ScannerService {
            val httpLogger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(httpLogger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ScannerService::class.java)
        }
    }
}