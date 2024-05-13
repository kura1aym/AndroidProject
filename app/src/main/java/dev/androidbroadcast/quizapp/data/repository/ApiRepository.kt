package dev.androidbroadcast.quizapp.data.repository

import dev.androidbroadcast.quizapp.data.network.ApiClient
import dev.androidbroadcast.quizapp.data.network.QuizApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

object ApiRepository {
    suspend fun fetchQuestionsWithPagination(amount: Int = 10, page: Int = 1): Response<QuizApiResponse> {
        return withContext(Dispatchers.IO) {
            ApiClient.quizApiService.fetchQuestions(amount, page)
        }
    }
}
