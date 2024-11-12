package dev.florian.linz.captainsmode.game;


import dev.florian.linz.captainsmode.utils.Role;

public record AddPlayerToGameRequest(
    long gameNumber,
    long playerId, 
    Role role,
    int kills,
    int deaths,
    int assists,
    String champion) {
}
