package dev.androidbroadcast.quizapp.data.mapper

import dev.androidbroadcast.quizapp.domain.model.Answer
import dev.androidbroadcast.quizapp.data.network.QuizApiResponse
import dev.androidbroadcast.quizapp.domain.model.Question
import dev.androidbroadcast.quizapp.domain.model.Quiz

fun mapApiResponseToQuiz(apiResponse: QuizApiResponse): Quiz {
    val questions = apiResponse.results.map { questionApi ->
        Question(
            question = questionApi.question,
            type = if (questionApi.type == "multiple") 0 else 1,
            answers = (
                    listOf(
                        Answer(questionApi.correct_answer, true)
                    ) + questionApi.incorrect_answers.map {
                        Answer(it, false)
                    }
                    ).toTypedArray()
        )
    }


    return Quiz(
        title = apiResponse.results.firstOrNull()?.category ?: "default",
        desc = "Generated Quiz",
        img = null,
        color = null,
        questions = questions.toTypedArray()
    )
}
