package dev.florian.linz.captainsmode.quiz;

import dev.florian.linz.captainsmode.api.ConfigurationController;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import dev.florian.linz.captainsmode.rest.error.ErrorCode;
import dev.florian.linz.captainsmode.utils.BaseService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuizService extends BaseService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public QuizService(AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }


    public Answer addAnswer(AddAnswerRequest request) {
        Optional<Answer> optionalAnswer = answerRepository.findByPersonAndQuestion(request.person(), request.question());
        if (optionalAnswer.isPresent()) {
            Answer answer = optionalAnswer.get();
            QuizMapper.mapAnswer(answer, request);
            return answer;
        } else {
            Answer answer = new Answer();
            QuizMapper.mapAnswer(answer, request);
            return answerRepository.save(answer);
        }
    }

    public List<GetParticipantResponse> getParticipants() {
        List<AnswerRepository.ParticipantsResponse> participantsResponse = answerRepository.getParticipants();
        return QuizMapper.mapParticipantsResponse(participantsResponse);
    }

    public Optional<Question> getQuestion() {
        return questionRepository.findByActive(true);
    }

    public Question addQuestion(AddQuestionRequest request) {
        Optional<Question> optionalQuestion = questionRepository.findByNumber(request.number());
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            QuizMapper.mapQuestion(question, request);
            return question;
        } else {
            Question question = new Question();
            QuizMapper.mapQuestion(question, request);
            return questionRepository.save(question);
        }
    }

    public void setQuestionToActive(int number) {
        List<Question> allQuestions = questionRepository.findAll();
        for (Question question : allQuestions) {
            question.setActive(false);
        }
        Optional<Question> question = questionRepository.findByNumber(number);
        if (question.isPresent()) {
            question.get().setActive(true);
        } else {
            throw new BadRequestException(ErrorCode.ENTITY_NOT_FOUND, "Question not found");
        }
    }
    
    public List<GetParticipantAnswerResponse> getParticipantAnswers(int number) {
        List<AnswerRepository.ParticipantAnswersResponse> participantAnswers = answerRepository.getParticipantAnswers(number);
        return QuizMapper.mapParticipantAnswersResponse(participantAnswers);
    }

    public void insertPoints(int number, List<AddPointsRequest> request) {
        for (AddPointsRequest addPointsRequest : request) {
            Optional<Answer> answer = answerRepository.findByPersonAndQuestion(addPointsRequest.participant(), number);
            answer.ifPresent(ans -> ans.setPoints(addPointsRequest.points())); 
        }
    }
}
