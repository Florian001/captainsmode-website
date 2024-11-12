package dev.florian.linz.captainsmode.api;

import java.math.BigDecimal;

public record Challenges(
    int abilityUses,
    int acesBefore15Minutes,
    int alliedJungleMonsterKills,
    int enemyJungleMonsterKills,
    BigDecimal damageTakenOnTeamPercentage,
    int hadOpenNexus,
    BigDecimal killParticipation,
    int killsUnderOwnTurret,
    int laneMinionsFirst10Minutes,
    int maxCsAdvantageOnLaneOpponent,
    int saveAllyFromDeath,
    int soloKills,
    int survivedSingleDigitHpCount,
    BigDecimal teamDamagePercentage
) {
}
