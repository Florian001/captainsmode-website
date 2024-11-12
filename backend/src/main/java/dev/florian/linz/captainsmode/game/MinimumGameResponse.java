package dev.florian.linz.captainsmode.game;

import dev.florian.linz.captainsmode.player.Player;
import java.time.LocalDateTime;
import java.util.List;

public record MinimumGameResponse(
    String matchId,
    long number,
    String description,
    LocalDateTime date,
    long durationInSeconds,
    Boolean win,
    Player captain,
    double bestCcv,
    List<MinimumGameParticipationResponse> participations) {
}
