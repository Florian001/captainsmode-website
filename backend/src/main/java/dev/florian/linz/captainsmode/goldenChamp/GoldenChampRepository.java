package dev.florian.linz.captainsmode.goldenChamp;

import dev.florian.linz.captainsmode.player.Player;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GoldenChampRepository extends JpaRepository<GoldenChampion, Long> {


    @Query("SELECT gc.wrongChampions FROM GoldenChampion gc")
    Set<String> getCurrentWrongChampions();

    @Query("SELECT gc FROM GoldenChampion gc where gc.gameFound is null")
    List<GoldenChampion> getCurrentGoldenChampRow();


    @Query("SELECT new dev.florian.linz.captainsmode.goldenChamp.GoldenChampOverview(gc.gameFound.number, gc.goldenChampion, gc.gameFound.captain.name) FROM GoldenChampion gc")
    List<GoldenChampOverview> getOverview();


  
}
