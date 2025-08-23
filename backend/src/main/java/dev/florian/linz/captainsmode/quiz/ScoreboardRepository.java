package dev.florian.linz.captainsmode.quiz;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreboardRepository extends JpaRepository<Scoreboard, Long> {
    
    @Query("Select s from Scoreboard s order by s.points desc")
    List<Scoreboard> findAllOrderedByPointsDesc();
}
