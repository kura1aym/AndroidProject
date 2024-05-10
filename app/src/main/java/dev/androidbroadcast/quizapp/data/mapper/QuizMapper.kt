package dev.androidbroadcast.quizapp.data.mapper

import dev.androidbroadcast.quizapp.data.model.Answer
import dev.androidbroadcast.quizapp.data.model.Question
import dev.androidbroadcast.quizapp.data.model.Quiz
import dev.androidbroadcast.quizapp.data.network.QuizApiResponse

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
        img = null, // Assuming the image is not provided in the API response
        color = null, // Assuming the color is not provided in the API response
        questions = questions.toTypedArray() // Convert list of Question to an array
    )
}
