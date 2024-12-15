package dev.florian.linz.captainsmode.quiz;

import dev.florian.linz.captainsmode.api.GameParticipationResponse;
import dev.florian.linz.captainsmode.api.GameResponse;
import dev.florian.linz.captainsmode.game.FullGameParticipationResponse;
import dev.florian.linz.captainsmode.game.FullGameResponse;
import dev.florian.linz.captainsmode.game.Game;
import dev.florian.linz.captainsmode.game.GameParticipation;
import dev.florian.linz.captainsmode.game.MinimumGameParticipationResponse;
import dev.florian.linz.captainsmode.game.MinimumGameResponse;
import dev.florian.linz.captainsmode.player.Player;
import dev.florian.linz.captainsmode.player.PlayerRepository;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import dev.florian.linz.captainsmode.rest.error.ErrorCode;
import dev.florian.linz.captainsmode.utils.Role;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {



    public QuizMapper() {
    }

    public static void mapAnswer(Answer answer, AddAnswerRequest request) {
        answer.setAnswer(request.answer());
        answer.setPerson(request.person());
        answer.setQuestion(request.question());
    }

    public static List<GetParticipantResponse> mapParticipantsResponse(List<AnswerRepository.ParticipantsResponse> participantsResponse) {
        List<GetParticipantResponse> participants = new ArrayList<>();
        for (AnswerRepository.ParticipantsResponse participant : participantsResponse) {
            participants.add(new GetParticipantResponse(participant.getName(), participant.getPoints()));
        }
        return participants;
    }

    public static void mapQuestion(Question question, AddQuestionRequest request) {
        question.setNumber(request.number());
        question.setQuestion(request.question());
        question.setActive(false);
        question.setCorrectAnswer(request.correctAnswer());
    }

    public static List<GetParticipantAnswerResponse> mapParticipantAnswersResponse(List<AnswerRepository.ParticipantAnswersResponse> participantAnswers) {
        List<GetParticipantAnswerResponse> participants = new ArrayList<>();
        for (AnswerRepository.ParticipantAnswersResponse participant : participantAnswers) {
            participants.add(new GetParticipantAnswerResponse(participant.getName(), participant.getAnswer(), participant.getPoints()));
        }
        return participants;
    }
}
