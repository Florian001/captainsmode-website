package dev.florian.linz.captainsmode.goldenChamp;


import dev.florian.linz.captainsmode.api.LolApiService;
import dev.florian.linz.captainsmode.game.MinimumGameResponse;
import dev.florian.linz.captainsmode.game.generalStats.GetStatsResponse;
import dev.florian.linz.captainsmode.player.PlayerService;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(GoldenChampController.BASE_URL)
@CrossOrigin
public class GoldenChampController {

    public static final String BASE_URL = "/api/v1/golden-champ";

    private static final Logger log = LoggerFactory.getLogger(GoldenChampController.class);

    private final GoldenChampService goldenChampService;
    
    public GoldenChampController(GoldenChampService goldenChampService) {
        this.goldenChampService = goldenChampService;
    }

    @GetMapping("/wrong-champs")
    @Transactional(readOnly = true)
    public ResponseEntity<Set<String>> getWrongChampions() {
        return new ResponseEntity<>(goldenChampService.getWrongChampions(), HttpStatus.OK);
    }

    @GetMapping("/overview")
    @Transactional(readOnly = true)
    public ResponseEntity<GoldenChampOverviewResponse> getGoldenChampOverview() {
        return new ResponseEntity<>(goldenChampService.getGoldenChampOverview(), HttpStatus.OK);
    }

    @GetMapping("/random-wrong-champ")
    @Transactional
    public ResponseEntity<String> addRandomWrongChamp() {
        return new ResponseEntity<>(goldenChampService.addRandomWrongChampion(), HttpStatus.OK);
    }
}
