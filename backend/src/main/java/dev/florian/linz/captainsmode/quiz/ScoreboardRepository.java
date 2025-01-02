package dev.florian.linz.captainsmode.quiz;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreboardRepository extends JpaRepository<Answer, Long> {
    
    Optional<Answer> findByPersonAndQuestion(String person, int question);
    
    
    @Query("""
    SELECT a.person as name, SUM(a.points) as points
    FROM Answer a
    group by a.person
    order by a.person
    """)
    List<ParticipantsResponse> getParticipants();

    @Query("""
    SELECT a.person as name, SUM(a.points) as points
    FROM Answer a
    group by a.person
    order by points desc
    limit 1
    """)
    Optional<ParticipantsResponse> getBestParticipants();


    @Query("""
    SELECT a.person as name, a.answer as answer, a.points as points
    FROM Answer a
    where a.question = :number
    order by a.person
    """)
    List<ParticipantAnswersResponse> getParticipantAnswers(int number);

    interface ParticipantAnswersResponse {
        String getName();
        String getAnswer();
        int getPoints();
    }

    
    interface ParticipantsResponse {
        String getName();
        int getPoints();
    }
}