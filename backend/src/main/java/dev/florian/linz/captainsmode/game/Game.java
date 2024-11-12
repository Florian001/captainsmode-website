package dev.florian.linz.captainsmode.game;

import dev.florian.linz.captainsmode.player.Player;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "games")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String matchId;
    
    private long number;
    
    private String description;
    
    private LocalDateTime date;
    
    private long durationInSeconds;

    private Boolean win;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private Player captain;

    @OneToMany(
        fetch = FetchType.EAGER,
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private final List<GameParticipation> participations = new ArrayList<>();
    

    public Game(long number) {
        this.number = number;
    }
    
    public void addParticipation(GameParticipation participation) {
        participation.setGame(this);
        participations.add(participation);
    }

    public void removeParticipation(GameParticipation participation) {
        participations.remove(participation);
    }

    public void setParticipation(List<GameParticipation> newParticipations) {
        this.participations.clear();
        this.participations.addAll(newParticipations);
    }
}
