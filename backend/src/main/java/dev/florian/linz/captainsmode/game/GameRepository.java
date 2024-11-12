package dev.florian.linz.captainsmode.game;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
    boolean existsGameByMatchId(String matchId);

    @Query("SELECT COUNT(game) = 0 FROM Game game")
    boolean isEmpty();
    
    @Query("""
    SELECT game.number from Game game ORDER BY game.number DESC LIMIT 1
    """)
    int getCurrentGameNumber();

    @Query("""
    SELECT game.matchId from Game game ORDER BY game.number DESC LIMIT :number
    """)
    List<String> getLastMatchIds(@Param("number") int number);

    @Query("""
    SELECT game from Game game ORDER BY game.number DESC
    """)
    List<Game> getAllOrderedByNumber();
    
    Optional<Game> getGameByNumber(long number);

    @Query("""
    SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*) as number
    from GameParticipation gp
    WHERE gp.game.win = true
    GROUP BY gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfWins();

    @Query("""
    SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*) as number
    from GameParticipation gp
    WHERE gp.game.win = false
    GROUP BY gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfLosses();

    @Query("""
    SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*) as number
    from GameParticipation gp
    GROUP BY gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfGamesPerPlayer();

    @Query("""
    SELECT COUNT(*)
    from Game
    """)
    Integer getNumberOfGamesTotal();

    @Query("""
        SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*)/20.0 as number
    from GameParticipation gp
    where gp.game.number > (
    SELECT MAX(gg.number)-20
    from Game gg
        )
    group by gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getParticipationLast20Games();

    @Query("""
        SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*) as number
    from GameParticipation gp
    where gp.game.captain = gp.player
    group by gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfCaptains();

    @Query("""
        SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*) as number
    from GameParticipation gp
    where gp.game.captain = gp.player
    and gp.game.win = true
    group by gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfCaptainWins();

//    @Query("""
//        SELECT games_with_ccv.playerOrder, games_with_ccv.captain_name as name, Max(games_with_ccv.ccv) as number
//        FROM (
//        SELECT g.captain.name as captain_name, g.captain.playerOrder as playerOrder, SUM(gp.kills) - SUM(gp.deaths) + SUM(gp.assists)/2.0 as ccv
//    FROM Game g
//LEFT JOIN Player p on p = g.captain
//LEFT JOIN GameParticipation gp on gp.game = g
//    where p.name <> 'Gegner'
//group by g.captain.name, g.captain.playerOrder
//) AS games_with_ccv
//group by games_with_ccv.captain_name, games_with_ccv.playerOrder
//ORDER BY games_with_ccv.playerOrder asc
//    """)
//    List<StatWithNumber> getBestGameAsCaptainCCV();

//    @Query("""
//        SELECT games_with_ccv.playerOrder, games_with_ccv.captain_name as name, games_with_ccv.gameNumber as number, Max(games_with_ccv.ccv) 
//        FROM (
//        SELECT g.captain.name as captain_name, g.number as gameNumber, p.name as n2, p.playerOrder as playerOrder, SUM(gp.kills) - SUM(gp.deaths) + SUM(gp.assists)/2.0 as ccv
//    FROM Game g
//LEFT JOIN Player p on p = g.captain
//LEFT JOIN GameParticipation gp on gp.game = g
//    where p.name <> 'Gegner'
//group by g.captain.name, g.number, p.name, p.playerOrder
//) AS games_with_ccv
//group by games_with_ccv.captain_name, games_with_ccv.playerOrder, games_with_ccv.gameNumber
//ORDER BY games_with_ccv.playerOrder asc
//    """)
//    List<StatWithNumber> getBestGameAsCaptainGameNumber();

    @Query("""
        SELECT p.playerOrder, g.captain.name as name, SUM(gp.kills) - SUM(gp.deaths) + SUM(gp.assists)/2.0 as number
    from GameParticipation gp
    left join Game g on g = gp.game
    left join Player p on p = g.captain
    where gp.player.name <> 'Gegner'
    group by g.captain.name, p.playerOrder
    ORDER BY p.playerOrder asc
    """)
    List<StatWithNumber> getAverageCCVPerPlayerIfCaptain();
    
    
    interface StatWithNumber {
        String getName();
        Number getNumber();
    }

    interface StatWithStringValue {
        String getName();
        String getStringValue();
    }
    
    
    
}
