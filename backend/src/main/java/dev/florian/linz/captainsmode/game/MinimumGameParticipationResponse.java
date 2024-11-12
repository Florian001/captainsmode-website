package dev.florian.linz.captainsmode.game;

import dev.florian.linz.captainsmode.player.Player;
import dev.florian.linz.captainsmode.utils.Role;
import java.math.BigDecimal;

public record MinimumGameParticipationResponse(
    Player player,
    double ccv,
    Role role,
    String championName,
    int kills,
    int deaths,
    int assists
) {}
