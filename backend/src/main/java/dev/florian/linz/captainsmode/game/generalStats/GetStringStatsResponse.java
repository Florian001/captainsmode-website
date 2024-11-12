package dev.florian.linz.captainsmode.game.generalStats;

import java.util.List;

public record GetStringStatsResponse(
    String displayName,
    List<GetStringStatOfPlayer> statsOfPlayers
) implements GetStatsResponse { }
