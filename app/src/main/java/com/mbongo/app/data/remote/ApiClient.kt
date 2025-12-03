package com.mbongo.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // IMPORTANT: Changez cette URL selon votre configuration réseau
    // - Pour émulateur Android: "http://10.0.2.2:5000/api/"
    // - Pour appareil physique sur le même WiFi: "http://VOTRE_IP_PC:5000/api/"
    
    private const val BASE_URL = "http://172.16.40.68:5000/api/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
    
    // Fonction pour changer l'URL dynamiquement (utile pour les paramètres)
    fun createApiServiceWithUrl(baseUrl: String): ApiService {
        val customRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return customRetrofit.create(ApiService::class.java)
    }
}
