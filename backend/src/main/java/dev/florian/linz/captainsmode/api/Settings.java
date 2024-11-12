package dev.florian.linz.captainsmode.api;

import dev.florian.linz.captainsmode.game.GameParticipation;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "settings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String apiKey;
    
}
