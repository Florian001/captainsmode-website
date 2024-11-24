package dev.florian.linz.captainsmode.api;


import dev.florian.linz.captainsmode.game.GameService;
import dev.florian.linz.captainsmode.goldenChamp.GoldenChampService;
import dev.florian.linz.captainsmode.player.PlayerService;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ConfigurationController.BASE_URL)
@CrossOrigin
public class ConfigurationController {

    public static final String BASE_URL = "/api/v1/";
    
    private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);
    
    private final GameService gameService;
    private final PlayerService playerService;
    private final LolApiService lolApiService;
    private final GoldenChampService goldenChampService;

    public ConfigurationController(GameService gameService, PlayerService playerService, LolApiService lolApiService, GoldenChampService goldenChampService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.lolApiService = lolApiService;
        this.goldenChampService = goldenChampService;
    }

    
    @PostMapping("syncWithApi/{playerName}")
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

    @PutMapping("add-api-key")
    @Transactional
    public void addApiKey(@RequestBody AddApiRequest request) {
        lolApiService.addApiKey(request.apiKey());
    }
    
    @DeleteMapping("{matchNumber}")
    @Transactional
    public void deleteGame(@PathVariable Long matchNumber) {
        gameService.deleteGame(matchNumber);
    }
    
}
