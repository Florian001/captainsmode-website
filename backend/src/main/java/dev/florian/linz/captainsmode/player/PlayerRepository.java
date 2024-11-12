package dev.florian.linz.captainsmode.player;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    @Query("select player from Player player where player.name <> 'Gegner' order by player.playerOrder ")
    List<Player> findAllPlayersExceptEnemy();

    @Query("select player from Player player order by player.playerOrder ")
    List<Player> findAllPlayersIncludingEnemy();
    
    Optional<Player> findPlayerByName(String name);
    Optional<Player> findPlayerById(Long id);
    Optional<Player> findPlayerByPuuid(String puuid);
}
