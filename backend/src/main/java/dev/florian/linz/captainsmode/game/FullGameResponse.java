package dev.florian.linz.captainsmode.game;

import dev.florian.linz.captainsmode.player.Player;
import java.time.LocalDateTime;
import java.util.List;

public record FullGameResponse(
    String matchId,
    long number,
    String description,
    LocalDateTime date,
    String duration,
    Boolean win,
    Player captain,
    double bestCcv,
    List<FullGameParticipationResponse> participations) {
}
