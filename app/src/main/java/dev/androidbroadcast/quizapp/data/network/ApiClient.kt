package dev.androidbroadcast.quizapp.data.network

import dev.maruffirdaus.kuizee.data.network.QuizApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://opentdb.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val quizApiService: QuizApiService by lazy {
        retrofit.create(QuizApiService::class.java)
    }
}

