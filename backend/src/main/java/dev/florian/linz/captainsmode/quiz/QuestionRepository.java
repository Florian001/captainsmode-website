package dev.florian.linz.captainsmode.quiz;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    Optional<Question> findByNumber(int number);

    Optional<Question> findByActive(boolean active);
}
