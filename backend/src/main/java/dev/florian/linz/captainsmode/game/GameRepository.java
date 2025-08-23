package dev.florian.linz.captainsmode.game;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import dev.florian.linz.captainsmode.game.generalStats.CumulativePointsDto;
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
    and DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    GROUP BY gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfWins(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    @Query("""
    SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*) as number
    from GameParticipation gp
    WHERE gp.game.win = false
    and DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    GROUP BY gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfLosses(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);


    @Query("""
    SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*) as number
    from GameParticipation gp
    where DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    GROUP BY gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfGamesPerPlayer(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    @Query("""
    SELECT COUNT(*)
    from Game g
    where DATE(g.date) >= :dateFrom
    and DATE(g.date) <= :dateTo
    """)
    Integer getNumberOfGamesTotal(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

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
    and DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    group by gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfCaptains(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
    
    @Query("""
        SELECT gp.player.playerOrder, gp.player.name as name, COUNT(*) as number
    from GameParticipation gp
    where gp.game.captain = gp.player
    and gp.game.win = true
    and DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    group by gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getNumberOfCaptainWins(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    @Query("""
        SELECT p.playerOrder, g.captain.name as name, SUM(gp.kills) - SUM(gp.deaths) + SUM(gp.assists)/2.0 as number
    from GameParticipation gp
    left join Game g on g = gp.game
    left join Player p on p = g.captain
    where gp.player.name <> 'Gegner'
    and DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    group by g.captain.name, p.playerOrder
    ORDER BY p.playerOrder asc
    """)
    List<StatWithNumber> getSumCCVPerPlayerIfCaptain(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
    
    @Query("""
        SELECT gp.player.playerOrder, gp.player.name as name, SUM(gp.kills) - SUM(gp.deaths) + SUM(gp.assists)/2.0 as number
    from GameParticipation gp
    where DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    group by gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getSumCCVPerPlayer(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    @Query("""
        SELECT gp.player.playerOrder, gp.player.name as name, SUM(gp.kills) - SUM(gp.deaths) + SUM(gp.assists)/2.0 as number
    from GameParticipation gp
    where DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    and gp.game.win = true
    group by gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getSumCCVPerPlayerOnWins(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    @Query("""
        SELECT gp.player.playerOrder, gp.player.name as name, SUM(gp.kills) - SUM(gp.deaths) + SUM(gp.assists)/2.0 as number
    from GameParticipation gp
    where DATE(gp.game.date) >= :dateFrom
    and DATE(gp.game.date) <= :dateTo
    and gp.game.win = false
    group by gp.player.name, gp.player.playerOrder
    ORDER BY gp.player.playerOrder asc
    """)
    List<StatWithNumber> getSumCCVPerPlayerOnLosses(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    @Query(value = """
        WITH scored AS (
            SELECT
                gp.game_id,
                gp.player_id,
                gp.kills - gp.deaths + gp.assists / 2.0 AS score
            FROM game_participation gp
            WHERE gp.player_id <> 1 -- exclude enemies
        ),
             ranked AS (
                 SELECT
                     s.*,
                     DENSE_RANK() OVER (PARTITION BY s.game_id ORDER BY s.score DESC) - 1 AS rnk
                 FROM scored s
             ),
             with_points AS (
                 SELECT
                     r.*,
                     CASE r.rnk
                         WHEN 0 THEN 2
                         WHEN 1 THEN 1
                         WHEN 2 THEN 0
                         WHEN 3 THEN -1
                         WHEN 4 THEN -2
                         END AS points
                 FROM ranked r
             ),
             all_points AS (
                 -- every game × every player (so missing participations show up as 0)
                 SELECT
                     g.id AS game_id,
                     p.id AS player_id,
                     COALESCE(wp.points, 0) AS points
                 FROM games g
                          CROSS JOIN players p
                          LEFT JOIN with_points wp
                                    ON wp.game_id = g.id AND wp.player_id = p.id
                 WHERE p.id <> 1  -- exclude the “enemy” player
             )
        SELECT
            g.number,
            ap.player_id as player,
            SUM(ap.points) OVER (
                PARTITION BY ap.player_id
                ORDER BY ap.game_id
                ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
                ) AS rankingpoints
        FROM all_points ap
        left join games g on ap.game_id = g.id
        ORDER BY ap.game_id, ap.player_id
    """,
    nativeQuery = true)
    List<RankingpointsStat> getRankingpointsPerGameOfPlayer();
    
    @Query(value = """
        WITH scored AS (
            SELECT
                gp.game_id,
                gp.player_id,
                gp.kills - gp.deaths + gp.assists / 2.0 AS score
            FROM game_participation gp
            WHERE gp.player_id <> 1 -- exclude enemies
        ),
             ranked AS (
                 SELECT
                     s.*,
                     DENSE_RANK() OVER (PARTITION BY s.game_id ORDER BY s.score DESC) - 1 AS rnk
                 FROM scored s
             ),
             with_points AS (
                 SELECT
                     r.*,
                     CASE r.rnk
                         WHEN 0 THEN 2
                         WHEN 1 THEN 1
                         WHEN 2 THEN 0
                         WHEN 3 THEN -1
                         WHEN 4 THEN -2
                         END AS points
                 FROM ranked r
             ),
             all_points AS (
                 -- every game × every player (so missing participations show up as 0)
                 SELECT
                     g.id AS game_id,
                     g.number AS game_number,
                     p.id AS player_id,
                     COALESCE(wp.points, 0) AS points
                 FROM games g
                          CROSS JOIN players p
                          LEFT JOIN with_points wp
                                    ON wp.game_id = g.id AND wp.player_id = p.id
                 WHERE p.id <> 1  -- exclude the “enemy” player
             )
        SELECT
            g.number,
            ap.player_id as player,
            SUM(ap.points) OVER (
                PARTITION BY ap.player_id
                ORDER BY ap.game_id
                ROWS BETWEEN 9 PRECEDING AND CURRENT ROW
                ) AS rankingpoints
        FROM all_points ap
        left join games g on ap.game_id = g.id
        ORDER BY ap.game_id, ap.player_id
    """,
    nativeQuery = true)
    List<RankingpointsStat> getRankingpointsPerLast10GameOfPlayer();


    interface StatWithNumber {
        String getName();
        Number getNumber();
    }

    interface StatWithStringValue {
        String getName();
        String getStringValue();
    }

    interface RankingpointsStat {
        int getNumber();
        int getPlayer();
        int getRankingpoints();
    }
    
    
    
}
