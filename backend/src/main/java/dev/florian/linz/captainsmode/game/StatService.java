package dev.florian.linz.captainsmode.game;

import dev.florian.linz.captainsmode.game.generalStats.StatWithNumberImplementation;
import dev.florian.linz.captainsmode.game.generalStats.StatWithStringValueImplementation;
import java.math.BigDecimal;
import org.jooq.SelectFieldOrAsterisk;
import static org.jooq.impl.DSL.*;
import dev.florian.linz.captainsmode.utils.BaseService;

import java.sql.Timestamp;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.partitionBy;
import static org.jooq.impl.DSL.quotedName;
import static org.jooq.impl.DSL.rowNumber;

import static org.jooq.impl.DSL.table;

import org.springframework.stereotype.Service;

@Service
public class StatService extends BaseService {

    public static final String PLAYERORDER = "playerorder";
    private final Table<?> gameTable = table("games");
    private final Field<Long> gameId = field("games.id", Long.class);
    private final Field<Long> gameNumber = field("games.number", Long.class);
    private final Field<Timestamp> gameDate = field("games.date", Timestamp.class);
    private final Field<Long> gameDurationInSeconds = field("games.duration_in_seconds", Long.class);
    private final Field<Boolean> gameWin = field("games.win", Boolean.class);
    private final Field<Long> gameCaptain_id = field("games.captain_id", Long.class);

    private final Table<?> participationTable = table("game_participation");
    private final Field<Long> participationGameId = field("game_participation.game_id", Long.class);
    private final Field<Long> participationPlayerId = field("game_participation.player_id", Long.class);
    private final Field<BigDecimal> kills = field("game_participation.kills", BigDecimal.class);
    private final Field<BigDecimal> deaths = field("game_participation.deaths", BigDecimal.class);
    private final Field<BigDecimal> assists = field("game_participation.assists", BigDecimal.class);

    private final Table<?> playerTable = table("players");
    private final Field<Long> playerId = field("players.id", Long.class);
    private final Field<String> playerName = field("players.name", String.class);
    private final Field<Integer> playerPlayerOrder = field("players.player_order", Integer.class);

    private final Field<BigDecimal> ccvValue = kills
        .sub(deaths)
        .add(assists.div(2.0));

    private final Field<BigDecimal> ccvValueField = field("ccvvalueinnertable", BigDecimal.class);
    
    private final Field<Integer> rowNumber = rowNumber()
        .over(partitionBy(gameNumber, gameWin, playerName)
            .orderBy(ccvValueField.desc())).as("rn");

    Field<Integer> rn = field("rn", Integer.class);
    private final Field<BigDecimal> maxValue = max(when(rn.eq(1), ccvValueField));
    private final Field<BigDecimal> secondMaxValue = max(when(rn.eq(2), ccvValueField));
    private final Field<BigDecimal> thirdMaxValue = max(when(rn.eq(3), ccvValueField));
    private final Field<BigDecimal> fourthMaxValue = max(when(rn.eq(4), ccvValueField));
    private final Field<BigDecimal> fifthMaxValue = max(when(rn.eq(5), ccvValueField));

    private final Field<BigDecimal> teamCCV = maxValue
        .add(secondMaxValue)
        .add(thirdMaxValue)
        .add(fourthMaxValue)
        .add(fifthMaxValue);
    
    
    private final Field<Long> id = field("id", Long.class);
    private final Field<Long> number = field("number", Long.class);
    private final Field<Timestamp> date = field("date", Timestamp.class);
    private final Field<Long> duration = field("duration", Long.class);
    private final Field<Boolean> win = field("win", Boolean.class);
    private final Field<String> captain = field("captain", String.class);
    private final Field<Integer> playerOrder = field(PLAYERORDER, Integer.class);
    private final DSLContext context;

    private final Table<?> enhancedGameTable;
    private final Table<?> enhancedParticipationTable;
    
    
    public StatService(DSLContext context) {
        this.context = context;
        this.enhancedGameTable = getEnhancedGameTable();
        this.enhancedParticipationTable = getEnhancedParticipationTable();
    }
    
    public List<GameRepository.StatWithNumber> getNumberOfCaptains() {
        return context.select(captain.as("name"), count(captain).as("number"))
            .from(enhancedGameTable)
            .groupBy(field("captain"), field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .fetchInto(StatWithNumberImplementation.class);
    }

    public List<GameRepository.StatWithNumber> getNumberOfCaptainWins() {
        return context.select(captain.as("name"), count(captain).as("number"))
            .from(enhancedGameTable)
            .where(win.eq(true))
            .groupBy(captain, field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .fetchInto(StatWithNumberImplementation.class);
    }
    
    public List<GameRepository.StatWithNumber> getAverageTeamCCVIfCaptain() {
        return context.select(captain.as("name"), avg(enhancedGameTable.field("teamCCV", BigDecimal.class)).as("number"))
            .from(enhancedGameTable)
            .groupBy(captain, field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .fetchInto(StatWithNumberImplementation.class);
    }
    
    public List<GameRepository.StatWithNumber> getBestTeamCCVIfCaptain() {
        return context.select(captain.as("name"), max(enhancedGameTable.field("teamCCV", BigDecimal.class)).as("number"))
            .from(enhancedGameTable)
            .groupBy(captain, field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .fetchInto(StatWithNumberImplementation.class);
    }

    public List<GameRepository.StatWithNumber> getBestTeamCCVIfCaptainGameNumber() {
        
        Table<?> maxCCV = context.select(captain.as("name"), max(enhancedGameTable.field("teamCCV", BigDecimal.class)).as("number"))
            .from(enhancedGameTable)
            .groupBy(captain, field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .asTable("max_ccv");
        
        Table<?> enhancedGameTableTwo = context.select().from(enhancedGameTable).asTable("egt");
        
        return context.select(captain.as("name"), max(field("egt.number")).as("number"))
            .from(enhancedGameTableTwo)
            .leftJoin(maxCCV).on(field("egt.captain", String.class).eq(field("max_ccv.name", String.class)))
            .where(enhancedGameTableTwo.field("teamCCV", BigDecimal.class).eq(field("max_ccv.number", BigDecimal.class)))
            .groupBy(field("egt.captain"), field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .fetchInto(StatWithNumberImplementation.class);
    }

    public List<GameRepository.StatWithNumber> getWorstTeamCCVIfCaptain() {
        return context.select(captain.as("name"), min(enhancedGameTable.field("teamCCV", BigDecimal.class)).as("number"))
            .from(enhancedGameTable)
            .groupBy(captain, field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .fetchInto(StatWithNumberImplementation.class);
    }

    public List<GameRepository.StatWithNumber> getWorstTeamCCVIfCaptainGameNumber() {

        Table<?> maxCCV = context.select(captain.as("name"), min(enhancedGameTable.field("teamCCV", BigDecimal.class)).as("number"))
            .from(enhancedGameTable)
            .groupBy(captain, field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .asTable("max_ccv");

        Table<?> enhancedGameTableTwo = context.select().from(enhancedGameTable).asTable("egt");

        return context.select(captain.as("name"), max(field("egt.number")).as("number"))
            .from(enhancedGameTableTwo)
            .leftJoin(maxCCV).on(field("egt.captain", String.class).eq(field("max_ccv.name", String.class)))
            .where(enhancedGameTableTwo.field("teamCCV", BigDecimal.class).eq(field("max_ccv.number", BigDecimal.class)))
            .groupBy(field("egt.captain"), field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .fetchInto(StatWithNumberImplementation.class);
    }

    public List<GameRepository.StatWithStringValue> getAverageGameLengthAsCaptain() {
        return context.select(captain.as("name"), avg(enhancedGameTable.field("duration", BigDecimal.class)).as("number"))
            .from(enhancedGameTable)
            .groupBy(captain, field(PLAYERORDER))
            .orderBy(field(PLAYERORDER))
            .fetchInto(StatWithStringValueImplementation.class);
    }

    public List<GameRepository.StatWithNumber> getAverageKillsPerPlayer() {
        return context.select(playerName.as("name"), avg(kills).as("number"))
            .from(participationTable)
            .leftJoin(playerTable).on(field("game_participation.player_id").eq(playerId))
            .groupBy(playerName, playerPlayerOrder)
            .orderBy(playerPlayerOrder)
            .fetchInto(StatWithNumberImplementation.class);
    }

    public List<GameRepository.StatWithNumber> getAverageDeathsPerPlayer() {
        return context.select(playerName.as("name"), avg(deaths).as("number"))
            .from(participationTable)
            .leftJoin(playerTable).on(field("game_participation.player_id").eq(playerId))
            .groupBy(playerName, playerPlayerOrder)
            .orderBy(playerPlayerOrder)
            .fetchInto(StatWithNumberImplementation.class);
    }

    public List<GameRepository.StatWithNumber> getAverageAssistsPerPlayer() {
        return context.select(playerName.as("name"), avg(assists).as("number"))
            .from(participationTable)
            .leftJoin(playerTable).on(field("game_participation.player_id").eq(playerId))
            .groupBy(playerName, playerPlayerOrder)
            .orderBy(playerPlayerOrder)
            .fetchInto(StatWithNumberImplementation.class);
    }

//    public List<GameRepository.StatWithNumber> getAverageCCVPerPlayer() {
//        return context.select(playerName.as("name"), avg(field("enhancedParticipationTable.ccv", BigDecimal.class)).as("number"))
//            .from(enhancedParticipationTable)
//            .leftJoin(playerTable).on(field("enhancedParticipationTable.player_id").eq(playerId))
//            .groupBy(playerName, playerPlayerOrder)
//            .orderBy(playerPlayerOrder)
//            .fetchInto(StatWithNumberImplementation.class);
//    }
//
//    public List<GameRepository.StatWithNumber> getAverageCCVOnWinsPerPlayer() {
//        return context.select(playerName.as("name"), avg(field("enhancedParticipationTable.ccv", BigDecimal.class)).as("number"))
//            .from(enhancedParticipationTable)
//            .leftJoin(playerTable).on(field("enhancedParticipationTable.player_id").eq(playerId))
//            .where(field("enhancedParticipationTable.win", Boolean.class).eq(true))
//            .groupBy(playerName, playerPlayerOrder)
//            .orderBy(playerPlayerOrder)
//            .fetchInto(StatWithNumberImplementation.class);
//    }
//
//    public List<GameRepository.StatWithNumber> getAverageCCVOnLossesPerPlayer() {
//        return context.select(playerName.as("name"), avg(field("enhancedParticipationTable.ccv", BigDecimal.class)).as("number"))
//            .from(enhancedParticipationTable)
//            .leftJoin(playerTable).on(field("enhancedParticipationTable.player_id").eq(playerId))
//            .where(field("enhancedParticipationTable.win", Boolean.class).eq(false))
//            .groupBy(playerName, playerPlayerOrder)
//            .orderBy(playerPlayerOrder)
//            .fetchInto(StatWithNumberImplementation.class);
//    }

    private Table<?> getEnhancedParticipationTable() {
        return context.select(enhancedGameTable.fields())
            .select(ccvValue.as("ccv"))
            .from(participationTable)
            .leftJoin(enhancedGameTable).on(field("game_participation.game_id", Long.class).eq(field("enhancedGameTable.id",Long.class)))
            .asTable("enhancedParticipationTable");
    }
    
    private Table<?> getEnhancedGameTable() {
        Table<?> tableWithRows = getTableWithRows();
        return context.select(
                tableWithRows.field("id", Long.class).as("id"),
                number.as("number"),
                date.as("date"),
                duration.as("duration"),
                win.as("win"),
                captain.as("captain"),
                playerOrder.as(PLAYERORDER),
                maxValue.as("maxValue"),
                secondMaxValue.as("secondMaxValue"),
                thirdMaxValue.as("thirdMaxValue"),
                fourthMaxValue.as("fourthMaxValue"),
                fifthMaxValue.as("fifthMaxValue"),
                teamCCV.as("teamCCV")
            )
            .from(tableWithRows)
            .groupBy(
                id,
                number,
                date,
                duration,
                win,
                captain,
                playerOrder)
            .asTable("enhancedGameTable");
    }
    
    private Table<?> getTableWithRows() {
        Table<?> innerTable = getInnerTable();

        return context.select(
                innerTable.field("id", Long.class).as("id"),
                number.as("number"),
                win.as("win"),
                date.as("date"),
                duration.as("duration"),
                captain.as("captain"),
                playerOrder.as(PLAYERORDER),
                ccvValueField.as("ccvvalueinnertable"),
                rowNumber()
                    .over(partitionBy(number, win, captain)
                        .orderBy(ccvValueField.desc())).as("rn"))
            .from(innerTable)
            .asTable("tablewithrows");
    }
    
    private Table<?> getInnerTable() {
        return context.select(
                field("games.id", Long.class).as("id"),
                gameNumber.as("number"),
                gameWin.as("win"),
                gameDate.as("date"),
                gameDurationInSeconds.as("duration"),
                playerName.as("captain"),
                playerPlayerOrder.as(PLAYERORDER),
                ccvValue.as("ccvvalueinnertable"))
            .from(gameTable)
            .leftJoin(participationTable).on(participationGameId.eq(gameId))
            .leftJoin(playerTable).on(gameCaptain_id.eq(playerId))
            .where(participationPlayerId.ne(1L))
            .asTable("innertable");
    }
    
}

//    public List<GameRepository.StatWithNumber> getNumberOfCaptainWins() {
//        
//    }