package dev.androidbroadcast.quizapp.domain.usecases

import android.util.Log
import dev.androidbroadcast.quizapp.data.network.QuizApiResponse
import dev.androidbroadcast.quizapp.data.repository.ApiRepository
import dev.androidbroadcast.quizapp.data.repository.QuizRepositoryImpl
import dev.androidbroadcast.quizapp.domain.repository.QuizRepository
import retrofit2.Response

interface FetchQuestionsUseCase {
    suspend fun execute(amount: Int, page: Int): Response<QuizApiResponse>
}

class FetchQuestionsUseCaseImpl(private val apiRepository: ApiRepository) : FetchQuestionsUseCase {
    override suspend fun execute(amount: Int, page: Int): Response<QuizApiResponse> {
        return apiRepository.fetchQuestionsWithPagination(amount, page)
    }
}

