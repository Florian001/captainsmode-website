package dev.florian.linz.captainsmode.api;


import dev.florian.linz.captainsmode.player.Player;
import dev.florian.linz.captainsmode.utils.BaseService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class LolApiService extends BaseService {
    
   
    private final RestTemplate restTemplate;
    private final SettingsRepository settingsRepository;

    public LolApiService(RestTemplate restTemplate, SettingsRepository settingsRepository) {
        this.restTemplate = restTemplate;
        this.settingsRepository = settingsRepository;
    }

    public List<String> lastGames(Player player) {
        String apiKey = settingsRepository.findAll().getFirst().getApiKey();
        String url = BASE_URL + "/lol/match/v5/matches/by-puuid/%s/ids?queue=440&start=0&count=5&api_key=%s".formatted(player.getPuuid(), apiKey);
        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
        Assert.isTrue(Objects.requireNonNull(response.getBody()).length > 0, "No Matches found!");
        return new ArrayList<>(Arrays.asList(response.getBody()));
    }
    
    public GameResponse getMatchData(String apiGameId) {
        String apiKey = settingsRepository.findAll().getFirst().getApiKey();
        String gameUrl = BASE_URL + "/lol/match/v5/matches/%s?api_key=%s".formatted(apiGameId, apiKey);
        ResponseEntity<GameResponse> response = restTemplate.getForEntity(gameUrl, GameResponse.class);
        assert response.getBody() != null;
        assert response.getStatusCode().is2xxSuccessful();
        return response.getBody();
    }

    public void addApiKey(String apiKey) {
        List<Settings> settings = settingsRepository.findAll();
        if (settings.isEmpty()) {
            Settings setting = new Settings();
            setting.setApiKey(apiKey);
            settingsRepository.save(setting);
        } else {
            settings.getFirst().setApiKey(apiKey);
            settingsRepository.flush();
        }
    }
}
