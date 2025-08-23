package dev.florian.linz.captainsmode.game;

import dev.florian.linz.captainsmode.api.ConfigurationController;
import dev.florian.linz.captainsmode.api.GameResponse;
import dev.florian.linz.captainsmode.api.LolApiService;
import static dev.florian.linz.captainsmode.game.GameMapper.ENEMY;

import dev.florian.linz.captainsmode.game.generalStats.CumulativePointsDto;
import dev.florian.linz.captainsmode.game.generalStats.GetNumberStatOfPlayer;
import dev.florian.linz.captainsmode.game.generalStats.GetNumberStatResponse;
import dev.florian.linz.captainsmode.game.generalStats.GetStatsResponse;
import dev.florian.linz.captainsmode.game.generalStats.GetStringStatOfPlayer;
import dev.florian.linz.captainsmode.game.generalStats.GetStringStatsResponse;
import dev.florian.linz.captainsmode.goldenChamp.GoldenChampService;
import dev.florian.linz.captainsmode.player.Player;
import dev.florian.linz.captainsmode.player.PlayerRepository;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import dev.florian.linz.captainsmode.rest.error.ErrorCode;
import dev.florian.linz.captainsmode.utils.BaseService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GameService extends BaseService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);

    @Value("#{'${lol.api.excluded.games}'.split(',')}")
    private List<String> excludedGameIds;
    
    private final LolApiService lolApiService;
    private final GameRepository gameRepository;
    private final StatService statService;
    private final GoldenChampService goldenChampService;
    
    private final GameMapper gameMapper;
    private final PlayerRepository playerRepository;

    public GameService(LolApiService lolApiService, GameRepository gameRepository, GameMapper gameMapper, PlayerRepository playerRepository, StatService statService, GoldenChampService goldenChampService) {
        this.lolApiService = lolApiService;
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
        this.playerRepository = playerRepository;
        this.statService = statService;
        this.goldenChampService = goldenChampService;
    }
    
    public List<MinimumGameResponse> getGames() {
        List<Game> games = gameRepository.getAllOrderedByNumber();
        sortParticipationWithRespectToPlayerOrder(games);
        return GameMapper.mapToMinimumGameResponse(games);
    }

    public FullGameResponse getGame(long matchNumber) {
        Optional<Game> game = gameRepository.getGameByNumber(matchNumber);
        if (game.isPresent()) {
            sortParticipationWithRespectToPlayerOrder(game.get());
            game.get().getParticipations().removeIf(Objects::isNull);
            return GameMapper.mapToResponse(game.get());
        } else {
            throw new BadRequestException(ErrorCode.ENTITY_NOT_FOUND, "game with number " + matchNumber + " not found");
        }
    }

    private void sortParticipationWithRespectToPlayerOrder(List<Game> games) {
        for (Game game : games) {
            sortParticipationWithRespectToPlayerOrder(game);
        }
    }

    private void sortParticipationWithRespectToPlayerOrder(Game game) {
        List<GameParticipation> fetchedParticipations = game.getParticipations();
        List<GameParticipation> orderedParticipations = new ArrayList<>();
        for (Player player : playerRepository.findAllPlayersExceptEnemy()){
            orderedParticipations.add(findParticipation(fetchedParticipations, player));
        }
        orderedParticipations.addAll(findEnemyParticipations(fetchedParticipations));
        game.setParticipation(orderedParticipations);
    }

    private GameParticipation findParticipation(List<GameParticipation> fetchedParticipations, Player player) {
        return fetchedParticipations.stream().filter(gp -> gp.getPlayer().equals(player)).findFirst().orElse(null);
    }

    private List<GameParticipation> findEnemyParticipations(List<GameParticipation> fetchedParticipations) {
        return fetchedParticipations.stream().filter(gp -> gp.getPlayer().getName().equals(ENEMY)).toList();
    }

    public Game insertGame(String matchId) {
        if (gameRepository.existsGameByMatchId(matchId)) {
            log.info("Game already exists");
            throw new BadRequestException(ErrorCode.MATCH_ALREADY_EXISTS, matchId + " already exists");
        }
        
        if (excludedGameIds.contains(matchId)) {
            log.info("Game is on blacklist");
            throw new BadRequestException(ErrorCode.MATCH_IS_BLACKLISTED, matchId + " is on blacklist");
        }
        
        GameResponse apiResponse = lolApiService.getMatchData(matchId);
        if (!apiResponse.info().gameMode().equals("CLASSIC")) {
            log.info("Unsupported game mode");
            throw new BadRequestException(ErrorCode.UNSUPPORTED_GAME_MODE, "Unsupported game mode");
        }
        log.info("Adding new game");
        int nextGameNumber = getNextGameNumber();
        Game newGame = new Game(nextGameNumber);
        gameMapper.mapGame(newGame, apiResponse);
        gameRepository.save(newGame);
        
        goldenChampService.processGoldenChamp(newGame);
        
        return newGame;
    }

    private int getNextGameNumber() {
        int nextGameNumber = 1;
        if (!gameRepository.isEmpty()) {
            nextGameNumber = gameRepository.getCurrentGameNumber() + 1;
        }
        return nextGameNumber;
    }
    
    public void deleteGame(Long matchNumber) {
        Optional<Game> gameToDelete = gameRepository.getGameByNumber(matchNumber);
        if (gameToDelete.isPresent()) {
            gameRepository.delete(gameToDelete.get());
            for (Game game : gameRepository.getAllOrderedByNumber()) {
                if (game.getNumber() > matchNumber) {
                    game.setNumber(game.getNumber() - 1);
                }
                if (game.getNumber() < matchNumber) {
                    return;
                }
            }
        } else {
            throw new BadRequestException(ErrorCode.ENTITY_NOT_FOUND, "Game with number " + matchNumber + " not found");
        }
    }

    public void setCaptainToGame(Long matchNumber, String playerName) {
        Optional<Game> game = gameRepository.getGameByNumber(matchNumber);
        Optional<Player> player = playerRepository.findPlayerByName(playerName);
        if (game.isPresent() && player.isPresent()) {
            game.get().setCaptain(player.get());
        } else {
            throw new BadRequestException(ErrorCode.ENTITY_NOT_FOUND, matchNumber + " as match number or " + playerName + " as player name not found");
        }
    }

    public void setDescriptionToMatch(Long matchNumber, String description) {
        Optional<Game> game = gameRepository.getGameByNumber(matchNumber);
        if (game.isPresent()) {
            game.get().setDescription(description);
        } else {
            throw new BadRequestException(ErrorCode.ENTITY_NOT_FOUND, matchNumber + " as match number not found");
        }
    }

    public void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new BadRequestException(ErrorCode.DESCRIPTION_EMPTY, "Description of a game needs to be something.");
        }
    }

    public List<GetStatsResponse> getStats(StatsType statsType, LocalDate dateFrom, LocalDate dateTo) {
        if (gameRepository.getNumberOfGamesTotal(dateFrom, dateTo) == 0) {
            throw new BadRequestException(ErrorCode.DATE_ERROR, "No matches found for this date range");
        }
        return switch (statsType) {
            case GENERAL -> getGeneralStats(dateFrom, dateTo);
            case CAPTAIN -> getCaptainStats(dateFrom, dateTo);
            case KDA -> getKDAStats(dateFrom, dateTo);
            case CS -> getCSStats(dateFrom, dateTo);
            case DAMAGE -> getDamageStats(dateFrom, dateTo);
        };
    }

    private List<GetStatsResponse> getGeneralStats(LocalDate dateFrom, LocalDate dateTo) {
        
        List<GetStatsResponse> stats = new ArrayList<>();
        
        stats.add(mapNumbersRepoResponse(gameRepository.getNumberOfGamesPerPlayer(dateFrom, dateTo), "Spiele", true, false, false));
        stats.add(mapNumbersRepoResponse(gameRepository.getNumberOfWins(dateFrom, dateTo), "Siege", true, false, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getNumberOfWins(dateFrom, dateTo), gameRepository.getNumberOfGamesPerPlayer(dateFrom, dateTo), "Winrate", true, true, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getNumberOfGamesPerPlayer(dateFrom, dateTo), gameRepository.getNumberOfGamesTotal(dateFrom, dateTo), "Gameparticipation", true, true, false));
        stats.add(mapNumbersRepoResponse(gameRepository.getParticipationLast20Games(), "Gameparticipation letzte 20 Spiele", true, true, false));
        return stats;
    }

    private List<GetStatsResponse> getCaptainStats(LocalDate dateFrom, LocalDate dateTo) {

        List<GetStatsResponse> stats = new ArrayList<>();

        stats.add(mapNumbersRepoResponse(gameRepository.getNumberOfCaptains(dateFrom, dateTo), "Anzahl Captain", true, false, false));
        stats.add(mapNumbersRepoResponse(gameRepository.getNumberOfCaptainWins(dateFrom, dateTo), "Anzahl Captainsiege", true, false, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getNumberOfCaptainWins(dateFrom, dateTo), gameRepository.getNumberOfCaptains(dateFrom, dateTo), "Captain winrate", true, true, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getNumberOfCaptains(dateFrom, dateTo), gameRepository.getNumberOfGamesPerPlayer(dateFrom, dateTo), "Als Captain teilgenommen", true, true, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getNumberOfCaptains(dateFrom, dateTo), gameRepository.getNumberOfGamesTotal(dateFrom, dateTo), "Captainanteil an allen Spielen", true, true, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getSumCCVPerPlayerIfCaptain(dateFrom, dateTo),gameRepository.getNumberOfCaptains(dateFrom, dateTo), "Ø Team-CCV als Captain", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getBestTeamCCVIfCaptain(dateFrom, dateTo),"Bestes Captainspiel (Team-CCV)", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getBestTeamCCVIfCaptainGameNumber(dateFrom, dateTo),"  -> in Spiel", null, false, false));
        stats.add(mapNumbersRepoResponse(statService.getWorstTeamCCVIfCaptain(dateFrom, dateTo),"Schlechtestes Captainspiel (Team-CCV)", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getWorstTeamCCVIfCaptainGameNumber(dateFrom, dateTo),"  -> in Spiel", null, false, false));
        stats.add(mapStringRepoResponse(statService.getAverageGameLengthAsCaptain(dateFrom, dateTo),"Ø Spielzeit als Captain in Minuten",false));

       return stats;
    }

    private List<GetStatsResponse> getKDAStats(LocalDate dateFrom, LocalDate dateTo) {

        List<GetStatsResponse> stats = new ArrayList<>();

        stats.add(mapNumbersRepoResponse(statService.getAverageKillsPerPlayer(dateFrom, dateTo), "Ø Kills", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getAverageDeathsPerPlayer(dateFrom, dateTo), "Ø Deaths", false, false, false));
        stats.add(mapNumbersRepoResponse(statService.getAverageAssistsPerPlayer(dateFrom, dateTo), "Ø Assists", true, false, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getSumCCVPerPlayer(dateFrom, dateTo),gameRepository.getNumberOfGamesPerPlayer(dateFrom, dateTo), "Ø ccv", true, false, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getSumCCVPerPlayerOnWins(dateFrom, dateTo),gameRepository.getNumberOfWins(dateFrom, dateTo), "Ø ccv bei Sieg", true, false, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getSumCCVPerPlayer(dateFrom, dateTo),gameRepository.getNumberOfLosses(dateFrom, dateTo), "Ø ccv bei Loss", true, false, false));
        return stats;
    }
    
    private GetStatsResponse mapNumbersRepoResponse(List<GameRepository.StatWithNumber> statForPlayers, String displayName, Boolean bestIsMax, boolean percentValue, boolean withEnemy) {
        List<GetNumberStatOfPlayer> playerStats = new ArrayList<>();
        double max =-10000.0;
        double min = 10000.0;
        for (GameRepository.StatWithNumber stat : statForPlayers) {
            double statValue = percentValue ? stat.getNumber().doubleValue()*100.0 : stat.getNumber().doubleValue();
            statValue = Math.round(statValue*100.0)/100.0;
            String player = stat.getName();
            boolean isEnemy = stat.getName().equals(ENEMY);
            if (withEnemy || !isEnemy) {
                playerStats.add(new GetNumberStatOfPlayer(player, statValue));
                if (statValue > max && !isEnemy) {
                    max = statValue;
                }
                if (statValue < min && !isEnemy) {
                    min = statValue;
                }
            }
        }
        double finalBest = bestIsMax == null ? 10000.0 : bestIsMax ? max : min;
        double finalWorst = bestIsMax == null ? 10000.0 : bestIsMax ? min : max;
        playerStats.forEach(ps -> {
            ps.setBest(ps.getValue() == finalBest);
            ps.setWorst(ps.getValue() == finalWorst);
        });
        return new GetNumberStatResponse(displayName, percentValue, playerStats);
    }
    
    private GetStatsResponse mapNumbersRepoResponseWithDivide(List<GameRepository.StatWithNumber> statForPlayersDividend, List<GameRepository.StatWithNumber> statForPlayersDivisor, String displayName, boolean bestIsMax, boolean percent, boolean withEnemy) {
        List<GetNumberStatOfPlayer> playerStats = new ArrayList<>();
        double max =-10000.0;
        double min = 10000.0;
        for (int i = 0; i < statForPlayersDividend.size(); i++) {
            double statValue =  (statForPlayersDividend.get(i).getNumber().doubleValue() / statForPlayersDivisor.get(i).getNumber().doubleValue());
            if (percent) {
                statValue *= 100.0;
            }
            statValue = Math.round(statValue*100.0)/100.0;
            String player = statForPlayersDividend.get(i).getName();
            boolean isEnemy = statForPlayersDividend.get(i).getName().equals(ENEMY);
            if (withEnemy || !isEnemy) {
                playerStats.add(new GetNumberStatOfPlayer(player, statValue));
                if (statValue > max && !isEnemy) {
                    max = statValue;
                }
                if (statValue < min && !isEnemy) {
                    min = statValue;
                }
            }
        }
        double finalBest = bestIsMax ? max : min;
        double finalWorst = bestIsMax ? min : max;
        playerStats.forEach(ps -> {
            ps.setBest(ps.getValue() == finalBest);
            ps.setWorst(ps.getValue() == finalWorst);
            
        });
        return new GetNumberStatResponse(displayName, percent, playerStats);
    }

    private GetStatsResponse mapNumbersRepoResponseWithDivide(List<GameRepository.StatWithNumber> statForPlayersDividend, Integer divisor, String displayName, boolean bestIsMax, boolean percent, boolean withEnemy) {
        List<GetNumberStatOfPlayer> playerStats = new ArrayList<>();
        double max =-10000.0;
        double min = 10000.0;
        for (int i = 0; i < statForPlayersDividend.size(); i++) {
            double statValue =  (statForPlayersDividend.get(i).getNumber().doubleValue() / divisor.doubleValue());
            if (percent) {
                statValue *= 100.0;
            }
            statValue = Math.round(statValue*100.0)/100.0;
            String player = statForPlayersDividend.get(i).getName();
            boolean isEnemy = statForPlayersDividend.get(i).getName().equals(ENEMY);
            if (withEnemy || !isEnemy) {
                playerStats.add(new GetNumberStatOfPlayer(player, statValue));
                if (statValue > max && !isEnemy) {
                    max = statValue;
                }
                if (statValue < min && !isEnemy) {
                    min = statValue;
                }
            }
        }
        double finalBest = bestIsMax ? max : min;
        double finalWorst = bestIsMax ? min : max;
        playerStats.forEach(ps -> {
            ps.setBest(ps.getValue() == finalBest);
            ps.setWorst(ps.getValue() == finalWorst);

        });
        return new GetNumberStatResponse(displayName, percent, playerStats);
    }

    private GetStatsResponse mapStringRepoResponse(List<GameRepository.StatWithStringValue> statForPlayers, String displayName, boolean withEnemy) {
        List<GetStringStatOfPlayer> playerStats = new ArrayList<>();

        for (int i = 0; i < statForPlayers.size(); i++) {
           
            String statValue = statForPlayers.get(i).getStringValue();
            String player = statForPlayers.get(i).getName();
            boolean isEnemy = statForPlayers.get(i).getName().equals(ENEMY);
            if (withEnemy || !isEnemy) {
                playerStats.add(new GetStringStatOfPlayer(player, statValue));
            }
        }
        return new GetStringStatsResponse(displayName, playerStats);
    }

    public List<GameRepository.RankingpointsStat> getRankingPointsResponse() {
        return gameRepository.getRankingpointsPerGameOfPlayer();
    }
    
    public List<GameRepository.RankingpointsStat> getRankingPointsLast10GamesResponse() {
        return gameRepository.getRankingpointsPerLast10GameOfPlayer();
    }
    
    private List<GetStatsResponse> getCSStats(LocalDate dateFrom, LocalDate dateTo) {
        return null;
    }

    private List<GetStatsResponse> getDamageStats(LocalDate dateFrom, LocalDate dateTo) {
        return null;
    }
}
