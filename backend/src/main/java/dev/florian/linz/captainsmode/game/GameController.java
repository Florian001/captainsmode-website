package dev.florian.linz.captainsmode.game;


import dev.florian.linz.captainsmode.api.LolApiService;
import dev.florian.linz.captainsmode.game.generalStats.GetStatsResponse;
import dev.florian.linz.captainsmode.player.PlayerService;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import dev.florian.linz.captainsmode.rest.error.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(GameController.BASE_URL)
@CrossOrigin
public class GameController {

    public static final String BASE_URL = "/api/v1/games";
    
    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    
    private final GameService gameService;

    private final PlayerService playerService;
    
    private final LolApiService lolApiService;

    public GameController(GameService gameService, PlayerService playerService, LolApiService lolApiService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.lolApiService = lolApiService;
    }


    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<MinimumGameResponse>> getGames() {
        return new ResponseEntity<>(gameService.getGames(), HttpStatus.OK);
    }

    @GetMapping("/{matchNumber}")
    @Transactional(readOnly = true)
    public ResponseEntity<FullGameResponse> getGames(@PathVariable long matchNumber) {
        return new ResponseEntity<>(gameService.getGame(matchNumber), HttpStatus.OK);
    }

    @PostMapping("/{matchId}")
    @Transactional
    public ResponseEntity<Game> insertGame(@PathVariable String matchId) {
        return new ResponseEntity<>(gameService.insertGame(matchId), HttpStatus.OK);
    }

    @PostMapping("/syncWithApi/{playerName}")
    @Transactional
    public void syncWithApi(@PathVariable String playerName) {
        List<String> matchHistory = lolApiService.lastGames(playerService.getPlayerByName(playerName)).reversed();
        for (String potentialNewGame : matchHistory) {
            try {
                gameService.insertGame(potentialNewGame);
            } catch (BadRequestException ignored) {
                log.info(String.format("Skipping %s", potentialNewGame));
            }
        }
    }
    
    @DeleteMapping("/{matchNumber}")
    @Transactional
    public void deleteGame(@PathVariable Long matchNumber) {
        gameService.deleteGame(matchNumber);
    }

    @PutMapping("/{matchNumber}/captain/{playerName}")
    @Transactional
    public void setCaptainToGame(@PathVariable Long matchNumber, @PathVariable String playerName) {
        gameService.setCaptainToGame(matchNumber, playerName);
    }

    @PutMapping("/{matchNumber}/description")
    @Transactional
    public void setDescriptionToGame(@PathVariable Long matchNumber, @RequestBody AddDescriptionRequest request) {
        gameService.validateDescription(request.description());
        gameService.setDescriptionToMatch(matchNumber, request.description());
    }

    @GetMapping("/stats/{statsType}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<GetStatsResponse>> getGames(
        @PathVariable StatsType statsType,
        @RequestParam(name = "dateFrom", required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @RequestParam(name = "dateTo", required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        
        LocalDate dateFromInclusive = dateFrom == null ? LocalDate.of(2000, 1, 1) : dateFrom;
        LocalDate dateToInclusive = dateFrom == null ? LocalDate.of(2100, 1, 1) : dateTo;
        if (dateFromInclusive.isAfter(dateTo)) {
            throw new BadRequestException(ErrorCode.DATE_ERROR, "Date from is after date to");
        }
        return new ResponseEntity<>(gameService.getStats(statsType, dateFromInclusive, dateToInclusive), HttpStatus.OK);
    }
}
