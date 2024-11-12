package dev.florian.linz.captainsmode.game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.florian.linz.captainsmode.api.Challenges;
import dev.florian.linz.captainsmode.player.Player;
import dev.florian.linz.captainsmode.utils.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "game_participation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameParticipation {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Player player;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @JsonBackReference
    private Game game;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    private String championName;
    private String summonerName;
    private int kills;
    private int deaths;
    private int assists;
    private int totalMinionsKilled;
    private int doubleKills;
    private int tripleKills;
    private int quadraKills;
    private int pentaKills;
    private int physicalDamageDealtToChampions;
    private int physicalDamageTaken;
    private int magicDamageDealtToChampions;
    private int magicDamageTaken;
    private int trueDamageDealtToChampions;
    private int trueDamageTaken;
    private int totalDamageDealtToChampions;
    private int totalDamageShieldedOnTeammates;
    private int totalDamageTaken;
    private boolean firstBloodAssist;
    private boolean firstBloodKill;
    private boolean firstTowerAssist;
    private boolean firstTowerKill;
    private int baronKills;
    private int champExperience;
    private int champLevel;
    private int damageDealtToBuildings;
    private int damageDealtToObjectives;
    private int damageDealtToTurrets;
    private int damageSelfMitigated;
    private int dragonKills;
    private int goldEarned;
    private int longestTimeSpentLiving;
    private int neutralMinionsKilled;
    private int nexusKills;
    private int objectivesStolen;
    private int totalAllyJungleMinionsKilled;
    private int totalEnemyJungleMinionsKilled;
    private int totalHeal;
    private int totalHealsOnTeammates;
    private int totalTimeCCDealt;
    private int totalTimeSpentDead;
    private int turretKills;
    private int detectorWardsPlaced;
    private int visionScore;
    private int wardsKilled;
    private int wardsPlaced;
    private int abilityUses;
    private int acesBefore15Minutes;
    private int alliedJungleMonsterKills;
    private int enemyJungleMonsterKills;
    @Column(columnDefinition = "DECIMAL(5,4)")
    private BigDecimal damageTakenOnTeamPercentage;
    private int hadOpenNexus;
    @Column(columnDefinition = "DECIMAL(5,4)")
    private BigDecimal killParticipation;
    private int killsUnderOwnTurret;
    private int laneMinionsFirst10Minutes;
    private int maxCsAdvantageOnLaneOpponent;
    private int saveAllyFromDeath;
    private int soloKills;
    private int survivedSingleDigitHpCount;
    
    @Column(columnDefinition = "DECIMAL(5,4)")
    private BigDecimal teamDamagePercentage;
   }
