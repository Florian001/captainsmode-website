package dev.florian.linz.captainsmode.goldenChamp;

import dev.florian.linz.captainsmode.api.ConfigurationController;
import dev.florian.linz.captainsmode.api.GameResponse;
import dev.florian.linz.captainsmode.api.LolApiService;
import dev.florian.linz.captainsmode.api.Settings;
import dev.florian.linz.captainsmode.api.SettingsRepository;
import dev.florian.linz.captainsmode.game.Game;
import static dev.florian.linz.captainsmode.game.GameMapper.ENEMY;
import dev.florian.linz.captainsmode.game.GameParticipation;
import dev.florian.linz.captainsmode.game.generalStats.GetNumberStatOfPlayer;
import dev.florian.linz.captainsmode.game.generalStats.GetNumberStatResponse;
import dev.florian.linz.captainsmode.game.generalStats.GetStatsResponse;
import dev.florian.linz.captainsmode.game.generalStats.GetStringStatOfPlayer;
import dev.florian.linz.captainsmode.game.generalStats.GetStringStatsResponse;
import dev.florian.linz.captainsmode.player.Player;
import dev.florian.linz.captainsmode.player.PlayerRepository;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import dev.florian.linz.captainsmode.rest.error.ErrorCode;
import static dev.florian.linz.captainsmode.rest.error.ErrorCode.ENTITY_NOT_FOUND;
import dev.florian.linz.captainsmode.utils.BaseService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoldenChampService extends BaseService {

    @Value("${lol.api.version}")
    protected String apiVersion;
    
    private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);
    
    private final RestTemplate restTemplate;
    
    private final SettingsRepository settingsRepository;
    private final GoldenChampRepository goldenChampRepository;
    

    public GoldenChampService(SettingsRepository settingsRepository, GoldenChampRepository goldenChampRepository) {
        this.settingsRepository = settingsRepository;
        this.goldenChampRepository = goldenChampRepository;
        this.restTemplate = new RestTemplate();
    }

    public List<String> getAllChampions() {
        String url = UriComponentsBuilder.fromHttpUrl("https://ddragon.leagueoflegends.com/cdn/{version}/data/en_US/champion.json")
            .buildAndExpand(apiVersion)
            .toUriString();
        
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        return new ArrayList<>(data.keySet());
    }

    public Set<String> getWrongChampions() {
        return goldenChampRepository.getCurrentWrongChampions();
    }

    public void processGoldenChamp(Game newGame) {
        List<GoldenChampion> entries = goldenChampRepository.getCurrentGoldenChampRow();
        if (entries.size() != 1) {
            throw new IllegalStateException("There should be exactly one row with a not-yet-found golden champion");
        } else {
            GoldenChampion entry = entries.getFirst();
            boolean found = checkIfGoldenChampWasPicked(newGame, entry);
            if (found) {
                entry.setGameFound(newGame);
                goldenChampRepository.save(entry);
                
                GoldenChampion nextGoldenChampion = new GoldenChampion();
                nextGoldenChampion.setGoldenChampion(getRandomChampion());
                nextGoldenChampion.setId(null);
                goldenChampRepository.save(nextGoldenChampion);
            } else {
                addChampsToWrongChamps(newGame, entry);
                addRandomAdditionalChampToWrongChamps(entry);
            }
        }
    }

    private String getRandomChampion() {
        List<String> champs = getAllChampions();
        Random rand = new Random();
        return champs.get(rand.nextInt(champs.size()));
    }
    
    private String getRandomNotGoldenChampion(GoldenChampion entry) {
        List<String> champs = getAllChampions();
        champs.removeAll(entry.getWrongChampions());
        champs.remove(entry.getGoldenChampion());
        
        if (champs.size() > 4) {
            Random rand = new Random();
            return champs.get(rand.nextInt(champs.size()));
        }
        return null;
    }
    
    private String addRandomAdditionalChampToWrongChamps(GoldenChampion entry) {
        String additionalChampToRemove = getRandomNotGoldenChampion(entry);
        if (additionalChampToRemove != null) {
            entry.addWrongChampions(List.of(additionalChampToRemove));
        }
        return additionalChampToRemove;
    }

    private boolean checkIfGoldenChampWasPicked(Game newGame, GoldenChampion entry) {
         return newGame.getParticipations().stream()
            .filter(p -> !p.getPlayer().getName().equals("Gegner"))
            .anyMatch(p -> p.getChampionName().equals(entry.getGoldenChampion()));
    }

    private void addChampsToWrongChamps(Game newGame, GoldenChampion entry) {
            List<String> champsPlayed = getPlayedChamps(newGame);
            entry.addWrongChampions(champsPlayed);
    }

    private List<String> getPlayedChamps(Game newGame) {
        return newGame.getParticipations().stream()
            .filter(p -> !p.getPlayer().getName().equals("Gegner"))
            .map(GameParticipation::getChampionName)
            .toList();
    }

    public GoldenChampOverviewResponse getGoldenChampOverview() {
        return new GoldenChampOverviewResponse(
            goldenChampRepository.getOverview()
        );
    }

    public String addRandomWrongChampion() {
        List<GoldenChampion> entries = goldenChampRepository.getCurrentGoldenChampRow();
        if (entries.size() != 1) {
            throw new IllegalStateException("There should be exactly one row with a not-yet-found golden champion");
        } else {
            GoldenChampion entry = entries.getFirst();
            return addRandomAdditionalChampToWrongChamps(entry);
        }
    }
}
