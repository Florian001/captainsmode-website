package dev.florian.linz.captainsmode.quiz;


import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(QuizController.BASE_URL)
@CrossOrigin
public class QuizController {

    public static final String BASE_URL = "/api/v1/quiz";
    
    private static final Logger log = LoggerFactory.getLogger(QuizController.class);
    
    private final QuizService quizService;
    
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }


    
    @PostMapping("/answer")
    @Transactional
    public ResponseEntity<Answer> addAnswer(@RequestBody AddAnswerRequest request) {
        return new ResponseEntity<>(quizService.addAnswer(request), HttpStatus.OK);
    }
    
    
    @GetMapping("/participants")
    @Transactional
    public ResponseEntity<List<GetParticipantResponse>> getParticipantsWithPoints() {
        return new ResponseEntity<>(quizService.getParticipants(), HttpStatus.OK);
    }

    @GetMapping("/question/{number}/answers")
    @Transactional
    public ResponseEntity<List<GetParticipantAnswerResponse>> getParticipantsWithAnswers(@PathVariable int number) {
        return new ResponseEntity<>(quizService.getParticipantAnswers(number), HttpStatus.OK);
    }

    @PostMapping("/question")
    @Transactional
    public ResponseEntity<Question> postQuestion(@RequestBody AddQuestionRequest request) {
        return new ResponseEntity<>(quizService.addQuestion(request), HttpStatus.OK);
    }
    
    @GetMapping("/question")
    @Transactional
    public ResponseEntity<Question> getQuestion() {
        Optional<Question> questionOptional = quizService.getQuestion();
        return questionOptional.map(question -> new ResponseEntity<>(question, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/question/{number}")
    @Transactional
    public void setQuestionToActive(@PathVariable int number) {
        quizService.setQuestionToActive(number);
    }

    @PostMapping("/question/{number}/points")
    @Transactional
    public void postPoints(@PathVariable int number, @RequestBody List<AddPointsRequest> request) {
        quizService.insertPoints(number, request);
    }
    
}
