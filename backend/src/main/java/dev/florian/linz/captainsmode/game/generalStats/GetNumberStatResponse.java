package dev.florian.linz.captainsmode.game.generalStats;

import java.util.List;

public record GetNumberStatResponse(
    String displayName,
    boolean percentValue,
    List<GetNumberStatOfPlayer> statsOfPlayers
) implements GetStatsResponse { }
