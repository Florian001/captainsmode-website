package dev.florian.linz.captainsmode.game;

import dev.florian.linz.captainsmode.api.GameParticipationResponse;
import dev.florian.linz.captainsmode.api.GameResponse;
import dev.florian.linz.captainsmode.player.Player;
import dev.florian.linz.captainsmode.player.PlayerRepository;
import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import dev.florian.linz.captainsmode.rest.error.ErrorCode;
import dev.florian.linz.captainsmode.utils.Role;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public static final String ENEMY = "Gegner";
    private final PlayerRepository playerRepo;

    public GameMapper(PlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }

    public static List<MinimumGameResponse> mapToMinimumGameResponse(List<Game> games) {
        List<MinimumGameResponse> response = new ArrayList<>();
        for (Game game : games) {
            response.add(mapToMinimumGameResponse(game));
        }
        return response;
    }

    public static MinimumGameResponse mapToMinimumGameResponse(Game game) {
        return new MinimumGameResponse(
            game.getMatchId(),
            game.getNumber(),
            game.getDescription(),
            game.getDate(),
            game.getDurationInSeconds(),
            game.getWin(),
            game.getCaptain(),
            calculateBestCCV(game),
            mapToMinimumParticipationResponse(game.getParticipations())
        );
    }
    
    public static FullGameResponse mapToResponse(Game game) {
        return new FullGameResponse(
            game.getMatchId(),
            game.getNumber(),
            game.getDescription(),
            game.getDate(),
            mapDuration(game.getDurationInSeconds()),
            game.getWin(),
            game.getCaptain(),
            calculateBestCCV(game),
            mapToParticipationResponse(game.getParticipations())
        );
    }

    private static String mapDuration(long durationInSeconds) {
        String minutes = String.valueOf(durationInSeconds / 60);
        String seconds = String.valueOf(durationInSeconds % 60 < 10 ? "0" + durationInSeconds % 60 : durationInSeconds % 60);
        return  minutes + ":" + seconds;
    }

    private static double calculateBestCCV(Game game) {
        return game.getParticipations().stream()
            .filter(Objects::nonNull)
            .filter(gp -> !gp.getPlayer().getName().equals(ENEMY))
            .map(GameMapper::calculateCCV)
            .mapToDouble(Double::doubleValue)
            .max()
            .orElseThrow();
    }

    private static double calculateCCV(GameParticipation participation) {
        if (participation == null) {
            return -1244;
        }
        return participation.getKills() - participation.getDeaths() + participation.getAssists()/2.0;
    }

    public static List<FullGameParticipationResponse> mapToParticipationResponse(List<GameParticipation> gameParticipations) {
        List<FullGameParticipationResponse> response = new ArrayList<>();
        for (GameParticipation gameParticipation : gameParticipations) {
            response.add(mapToResponse(gameParticipation));
        }
        return response;
    }

    public static List<MinimumGameParticipationResponse> mapToMinimumParticipationResponse(List<GameParticipation> gameParticipations) {
        List<MinimumGameParticipationResponse> response = new ArrayList<>();
        for (GameParticipation gameParticipation : gameParticipations) {
            response.add(mapToMinimumResponse(gameParticipation));
        }
        return response;
    }

    public static FullGameParticipationResponse mapToResponse(GameParticipation participation) {
        if (participation == null) {
            return null;
        }
        return new FullGameParticipationResponse(
            participation.getPlayer(),
            calculateCCV(participation),
            participation.getRole(),
            participation.getChampionName(),
            participation.getSummonerName(),
            participation.getKills(),
            participation.getDeaths(),
            participation.getAssists(),
            participation.getTotalMinionsKilled(),
            participation.getDoubleKills(),
            participation.getTripleKills(),
            participation.getQuadraKills(),
            participation.getPentaKills(),
            participation.getPhysicalDamageDealtToChampions(),
            participation.getPhysicalDamageTaken(),
            participation.getMagicDamageDealtToChampions(),
            participation.getMagicDamageTaken(),
            participation.getTrueDamageDealtToChampions(),
            participation.getTrueDamageTaken(),
            participation.getTotalDamageDealtToChampions(),
            participation.getTotalDamageShieldedOnTeammates(),
            participation.getTotalDamageTaken(),
            participation.isFirstBloodAssist(),
            participation.isFirstBloodKill(),
            participation.isFirstTowerAssist(),
            participation.isFirstTowerKill(),
            participation.getBaronKills(),
            participation.getChampExperience(),
            participation.getChampLevel(),
            participation.getDamageDealtToBuildings(),
            participation.getDamageDealtToObjectives(),
            participation.getDamageDealtToTurrets(),
            participation.getDamageSelfMitigated(),
            participation.getDragonKills(),
            participation.getGoldEarned(),
            participation.getLongestTimeSpentLiving(),
            participation.getNeutralMinionsKilled(),
            participation.getNexusKills(),
            participation.getObjectivesStolen(),
            participation.getTotalAllyJungleMinionsKilled(),
            participation.getTotalEnemyJungleMinionsKilled(),
            participation.getTotalHeal(),
            participation.getTotalHealsOnTeammates(),
            participation.getTotalTimeCCDealt(),
            participation.getTotalTimeSpentDead(),
            participation.getTurretKills(),
            participation.getDetectorWardsPlaced(),
            participation.getVisionScore(),
            participation.getWardsKilled(),
            participation.getWardsPlaced(),
            participation.getAbilityUses(),
            participation.getAcesBefore15Minutes(),
            participation.getAlliedJungleMonsterKills(),
            participation.getEnemyJungleMonsterKills(),
            participation.getDamageTakenOnTeamPercentage(),
            participation.getHadOpenNexus(),
            participation.getKillParticipation(),
            participation.getKillsUnderOwnTurret(),
            participation.getLaneMinionsFirst10Minutes(),
            participation.getMaxCsAdvantageOnLaneOpponent(),
            participation.getSaveAllyFromDeath(),
            participation.getSoloKills(),
            participation.getSurvivedSingleDigitHpCount(),
            participation.getTeamDamagePercentage()
        );
    }

    public static MinimumGameParticipationResponse mapToMinimumResponse(GameParticipation participation) {
        if (participation == null) {
            return null;
        }
        return new MinimumGameParticipationResponse(
            participation.getPlayer(),
            calculateCCV(participation),
            participation.getRole(),
            participation.getChampionName(),
            participation.getKills(),
            participation.getDeaths(),
            participation.getAssists()
        );
    }


    public void mapGame(Game newGame, GameResponse apiResponse) {
        int enemyCount = 0;
        newGame.setMatchId(apiResponse.metadata().matchId());
        newGame.setDate(mapLongToLocalDateTime(apiResponse.info().gameCreation()));
        newGame.setDurationInSeconds(apiResponse.info().gameDuration());
        newGame.setWin(extractWin(apiResponse.info().participants()));
        for (GameParticipationResponse response : apiResponse.info().participants()) {
            GameParticipation participation = new GameParticipation();
            mapGameParticipation(participation, response);
            Player player = mapPuuidToPlayer(response.puuid(), newGame.getWin() == response.win());
            if (player.getName().equals(ENEMY)) {
                enemyCount++;
                if (enemyCount > 5) {
                    throw new BadRequestException(ErrorCode.NOW_ENOUGH_PLAYERS, "Cannot find 5 registered player for this game!");
                }
            }
            participation.setPlayer(player);
            newGame.addParticipation(participation);
        }
    }

    private Boolean extractWin(List<GameParticipationResponse> participants) {
        for (GameParticipationResponse response : participants) {
            Player player = mapPuuidToPlayer(response.puuid(), false);
            if (!player.getName().equals(ENEMY) ) {
                return response.win();
            }
        }
        throw new IllegalStateException("Game full of enemies.");
    }

    private Player mapPuuidToPlayer(String puuid, boolean usePotentiallyWildcard) {
        Optional<Player> player = playerRepo.findPlayerByPuuid(puuid);
        if (player.isPresent()) {
            return player.get();
        }
        if (usePotentiallyWildcard) {
            return playerRepo.findPlayerById(52L).orElseThrow();
        }
        return playerRepo.findPlayerByName(ENEMY).orElseThrow();
    }

    private static void mapGameParticipation(GameParticipation participation, GameParticipationResponse response) {
        participation.setKills(response.kills());
        participation.setDeaths(response.deaths());
        participation.setAssists(response.assists());
        participation.setRole(mapRole(response.teamPosition()));
        participation.setChampionName(response.championName());
        participation.setSummonerName(response.summonerName());
        participation.setTotalMinionsKilled(response.totalMinionsKilled());
        participation.setDoubleKills(response.doubleKills());
        participation.setTripleKills(response.tripleKills());
        participation.setQuadraKills(response.quadraKills());
        participation.setPentaKills(response.pentaKills());
        participation.setPhysicalDamageDealtToChampions(response.physicalDamageDealtToChampions());
        participation.setPhysicalDamageTaken(response.physicalDamageTaken());
        participation.setMagicDamageDealtToChampions(response.magicDamageDealtToChampions());
        participation.setMagicDamageTaken(response.magicDamageTaken());
        participation.setTrueDamageDealtToChampions(response.trueDamageDealtToChampions());
        participation.setTrueDamageTaken(response.trueDamageTaken());
        participation.setTotalDamageDealtToChampions(response.totalDamageDealtToChampions());
        participation.setTotalDamageShieldedOnTeammates(response.totalDamageShieldedOnTeammates());
        participation.setTotalDamageTaken(response.totalDamageTaken());
        participation.setFirstBloodAssist(response.firstBloodAssist());
        participation.setFirstBloodKill(response.firstBloodKill());
        participation.setFirstTowerAssist(response.firstTowerAssist());
        participation.setFirstTowerKill(response.firstTowerKill());
        participation.setBaronKills(response.baronKills());
        participation.setChampExperience(response.champExperience());
        participation.setChampLevel(response.champLevel());
        participation.setDamageDealtToBuildings(response.damageDealtToBuildings());
        participation.setDamageDealtToObjectives(response.damageDealtToObjectives());
        participation.setDamageDealtToTurrets(response.damageDealtToTurrets());
        participation.setDamageSelfMitigated(response.damageSelfMitigated());
        participation.setDragonKills(response.dragonKills());
        participation.setGoldEarned(response.goldEarned());
        participation.setLongestTimeSpentLiving(response.longestTimeSpentLiving());
        participation.setNeutralMinionsKilled(response.neutralMinionsKilled());
        participation.setNexusKills(response.nexusKills());
        participation.setObjectivesStolen(response.objectivesStolen());
        participation.setTotalAllyJungleMinionsKilled(response.totalAllyJungleMinionsKilled());
        participation.setTotalEnemyJungleMinionsKilled(response.totalEnemyJungleMinionsKilled());
        participation.setTotalHeal(response.totalHeal());
        participation.setTotalHealsOnTeammates(response.totalHealsOnTeammates());
        participation.setTotalTimeCCDealt(response.totalTimeCCDealt());
        participation.setTotalTimeSpentDead(response.totalTimeSpentDead());
        participation.setTurretKills(response.turretKills());
        participation.setDetectorWardsPlaced(response.detectorWardsPlaced());
        participation.setVisionScore(response.visionScore());
        participation.setWardsKilled(response.wardsKilled());
        participation.setWardsPlaced(response.wardsPlaced());
        participation.setAbilityUses(response.challenges().abilityUses());
        participation.setAcesBefore15Minutes(response.challenges().acesBefore15Minutes());
        participation.setAlliedJungleMonsterKills(response.challenges().alliedJungleMonsterKills());
        participation.setEnemyJungleMonsterKills(response.challenges().enemyJungleMonsterKills());
        participation.setDamageTakenOnTeamPercentage(response.challenges().damageTakenOnTeamPercentage());
        participation.setHadOpenNexus(response.challenges().hadOpenNexus());
        participation.setKillParticipation(response.challenges().killParticipation());
        participation.setKillsUnderOwnTurret(response.challenges().killsUnderOwnTurret());
        participation.setLaneMinionsFirst10Minutes(response.challenges().laneMinionsFirst10Minutes());
        participation.setMaxCsAdvantageOnLaneOpponent(response.challenges().maxCsAdvantageOnLaneOpponent());
        participation.setSaveAllyFromDeath(response.challenges().saveAllyFromDeath());
        participation.setSoloKills(response.challenges().soloKills());
        participation.setSurvivedSingleDigitHpCount(response.challenges().survivedSingleDigitHpCount());
        participation.setTeamDamagePercentage(response.challenges().teamDamagePercentage());

    }

    private static Role mapRole(String teamPosition) {
        return switch (teamPosition) {
            case "TOP" -> Role.TOP;
            case "JUNGLE" -> Role.JGL;
            case "MIDDLE" -> Role.MID;
            case "BOTTOM" -> Role.ADC;
            case "UTILITY" -> Role.SUP;
            default -> Role.UNKNOWN;
        };
    }

    private static LocalDateTime mapLongToLocalDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("Europe/Berlin"));
    }
}
