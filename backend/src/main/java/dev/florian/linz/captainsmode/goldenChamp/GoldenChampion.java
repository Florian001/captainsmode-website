package dev.florian.linz.captainsmode.goldenChamp;

import dev.florian.linz.captainsmode.game.Game;
import dev.florian.linz.captainsmode.game.GameParticipation;
import dev.florian.linz.captainsmode.player.Player;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "golden_champion")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoldenChampion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String goldenChampion;
    
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Game gameFound;
    
    @ElementCollection
    @JoinColumn(name = "golden_champ_id")
    private final Set<String> wrongChampions = new HashSet<>();
    
   public void addWrongChampions(List<String> champions) {
       wrongChampions.addAll(champions);
   }
}
