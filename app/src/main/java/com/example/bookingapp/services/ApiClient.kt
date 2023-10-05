package com.example.bookingapp.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        private const val BASE_URL = "https://demo.voltlines.com/"
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }

        fun handleFailure(errorMessage: String) {
            // Hata'yı konsola yaz
            println("ApiClient Error: $errorMessage")
        }
    }
}