package dev.florian.linz.captainsmode.api;

import java.util.List;

public record GameInfo(
    int gameDuration,
    long gameCreation,
    String gameMode,
    List<GameParticipationResponse> participants
) {
}
