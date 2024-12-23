package dev.florian.linz.captainsmode.quiz;

import java.util.List;

public record GetWholeQuizResponse(String question, String correctAnswer, List<GetParticipantAnswerResponse> playerAnswers) {
}
