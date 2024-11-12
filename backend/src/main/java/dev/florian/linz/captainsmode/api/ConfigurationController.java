package dev.florian.linz.captainsmode.api;


import dev.florian.linz.captainsmode.game.AddApiRequest;
import dev.florian.linz.captainsmode.game.GameService;
import dev.florian.linz.captainsmode.player.PlayerService;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
//    private JwtUtil jwtUtil = new JwtUtil();
    private String correctPassword = "your-password";
    
    private final GameService gameService;

    private final PlayerService playerService;
    
    private final LolApiService lolApiService;

    public ConfigurationController(GameService gameService, PlayerService playerService, LolApiService lolApiService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.lolApiService = lolApiService;
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

//    @PostMapping("login")
//    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
//        String password = loginData.get("password");
//
//        if (correctPassword.equals(password)) {
//            String token = jwtUtil.generateToken("user"); // You can add more details as required
//            return ResponseEntity.ok(Collections.singletonMap("token", token));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
//        }
//    }
    
}
