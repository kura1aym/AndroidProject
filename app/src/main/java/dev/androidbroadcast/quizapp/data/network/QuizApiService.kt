package dev.androidbroadcast.quizapp.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
interface QuizApiService {
    @GET("api.php")
    suspend fun fetchQuestions(
        @Query("amount") amount: Int = 10,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<QuizApiResponse>
}

data class QuizApiResponse(
    val response_code: Int,
    val results: List<QuestionApi>
)

data class QuestionApi(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)


