package dev.florian.linz.captainsmode.player;


import dev.florian.linz.captainsmode.api.GameResponse;
import dev.florian.linz.captainsmode.api.LolApiService;
import dev.florian.linz.captainsmode.game.GameService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PlayerController.BASE_URL)
@CrossOrigin
public class PlayerController {

    public static final String BASE_URL = "/api/v1/players";
    
    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private LolApiService lolApiService;
    
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return new ResponseEntity<>(playerService.allPlayers(), HttpStatus.OK);
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<Player> getPlayerByName(@PathVariable String name) {
        return new ResponseEntity<>(playerService.getPlayerByName(name), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody CreatePlayerRequest request) {
        return new ResponseEntity<>(playerService.createPlayer(request.name(), request.puuid()), HttpStatus.OK); 
    }

    @GetMapping("/{name}/match-history")
    public ResponseEntity<List<String>> getMatchHistoryByPlayer(@PathVariable String name) {
        List<String> response = lolApiService.lastGames(playerService.getPlayerByName(name));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
