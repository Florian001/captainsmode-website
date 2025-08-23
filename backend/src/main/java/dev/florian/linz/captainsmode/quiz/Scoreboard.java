package dev.florian.linz.captainsmode.quiz;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scoreboard")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Scoreboard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String person;
    
    private int points;
    
    private LocalDateTime dateTime;
    
}
