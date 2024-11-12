package dev.florian.linz.captainsmode.game;

import dev.florian.linz.captainsmode.api.GameResponse;
import dev.florian.linz.captainsmode.api.LolApiService;
import static dev.florian.linz.captainsmode.game.GameMapper.ENEMY;
import dev.florian.linz.captainsmode.game.generalStats.GetNumberStatOfPlayer;
import dev.florian.linz.captainsmode.game.generalStats.GetNumberStatResponse;
import dev.florian.linz.captainsmode.game.generalStats.GetStatsResponse;
import dev.florian.linz.captainsmode.game.generalStats.GetStringStatOfPlayer;
import dev.florian.linz.captainsmode.game.generalStats.GetStringStatsResponse;
import dev.florian.linz.captainsmode.player.Player;
import dev.florian.linz.captainsmode.player.PlayerRepository;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import dev.florian.linz.captainsmode.rest.error.ErrorCode;
import dev.florian.linz.captainsmode.utils.BaseService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GameService extends BaseService {
    
    
    private final LolApiService lolApiService;
    
    private final GameRepository gameRepository;

    private final StatService statService;
    
    private final GameMapper gameMapper;
    private final PlayerRepository playerRepository;

    public GameService(LolApiService lolApiService, GameRepository gameRepository, GameMapper gameMapper, PlayerRepository playerRepository, StatService statService) {
        this.lolApiService = lolApiService;
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
        this.playerRepository = playerRepository;
        this.statService = statService;
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
            throw new BadRequestException(ErrorCode.MATCH_ALREADY_EXISTS, matchId + " already exists");
        }
        
        GameResponse apiResponse = lolApiService.getMatchData(matchId);
        if (!apiResponse.info().gameMode().equals("CLASSIC")) {
            throw new BadRequestException(ErrorCode.UNSUPPORTED_GAME_MODE, "Unsupported game mode");
        }
        int nextGameNumber = getNextGameNumber();
        Game newGame = new Game(nextGameNumber);
        gameMapper.mapGame(newGame, apiResponse);
        return gameRepository.save(newGame);
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

    public List<GetStatsResponse> getStats(StatsType statsType) {
        return switch (statsType) {
            case GENERAL -> getGeneralStats();
            case CAPTAIN -> getCaptainStats();
            case KDA -> getKDAStats();
            case CS -> getCSStats();
            case DAMAGE -> getDamageStats();
        };
    }

    private List<GetStatsResponse> getGeneralStats() {
        
        List<GetStatsResponse> stats = new ArrayList<>();
        
        stats.add(mapNumbersRepoResponse(gameRepository.getNumberOfGamesPerPlayer(), "Spiele", true, false, false));
        stats.add(mapNumbersRepoResponse(gameRepository.getNumberOfWins(), "Siege", true, false, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getNumberOfWins(), gameRepository.getNumberOfGamesPerPlayer(), "Winrate", true, true, false));
        stats.add(mapNumbersRepoResponseWithDivide(gameRepository.getNumberOfGamesPerPlayer(), gameRepository.getNumberOfGamesTotal(), "Gameparticipation", true, true, false));
        stats.add(mapNumbersRepoResponse(gameRepository.getParticipationLast20Games(), "Gameparticipation letzte 20 Spiele", true, true, false));
        return stats;
    }

    private List<GetStatsResponse> getCaptainStats() {

        List<GetStatsResponse> stats = new ArrayList<>();

        stats.add(mapNumbersRepoResponse(statService.getNumberOfCaptains(), "Anzahl Captain", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getNumberOfCaptainWins(), "Anzahl Captainsiege", true, false, false));
        stats.add(mapNumbersRepoResponseWithDivide(statService.getNumberOfCaptainWins(), statService.getNumberOfCaptains(), "Captain winrate", true, true, false));
        stats.add(mapNumbersRepoResponseWithDivide(statService.getNumberOfCaptains(), gameRepository.getNumberOfGamesPerPlayer(), "Als Captain teilgenommen", true, true, false));
        stats.add(mapNumbersRepoResponseWithDivide(statService.getNumberOfCaptains(), gameRepository.getNumberOfGamesTotal(), "Captainanteil an allen Spielen", true, true, false));
        stats.add(mapNumbersRepoResponse(statService.getAverageTeamCCVIfCaptain(),"Ø Team-CCV als Captain", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getBestTeamCCVIfCaptain(),"Bestes Captainspiel (Team-CCV)", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getBestTeamCCVIfCaptainGameNumber(),"  -> in Spiel", null, false, false));
        stats.add(mapNumbersRepoResponse(statService.getWorstTeamCCVIfCaptain(),"Bestes Captainspiel (Team-CCV)", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getWorstTeamCCVIfCaptainGameNumber(),"  -> in Spiel", null, false, false));
        stats.add(mapStringRepoResponse(statService.getAverageGameLengthAsCaptain(),"Ø Spielzeit als Captain",false));

       return stats;
    }

    private List<GetStatsResponse> getKDAStats() {

        List<GetStatsResponse> stats = new ArrayList<>();

        stats.add(mapNumbersRepoResponse(statService.getAverageKillsPerPlayer(), "Ø Kills", true, false, false));
        stats.add(mapNumbersRepoResponse(statService.getAverageDeathsPerPlayer(), "Ø Deaths", false, false, false));
        stats.add(mapNumbersRepoResponse(statService.getAverageAssistsPerPlayer(), "Ø Assists", true, false, false));
//        stats.add(mapNumbersRepoResponse(statService.getAverageCCVPerPlayer(), "Ø ccv", true, false, false));
//        stats.add(mapNumbersRepoResponse(statService.getAverageCCVOnWinsPerPlayer(), "Ø ccv bei Sieg", true, false, false));
//        stats.add(mapNumbersRepoResponse(statService.getAverageCCVOnLossesPerPlayer(), "Ø ccv bei Loss", true, false, false));
        

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
    
    private List<GetStatsResponse> getCSStats() {
        return null;
    }

    private List<GetStatsResponse> getDamageStats() {
        return null;
    }
}
