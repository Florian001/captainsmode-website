package dev.florian.linz.captainsmode.game.generalStats;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetNumberStatOfPlayer {
    String playerName;
    double value;
    Boolean best;
    Boolean worst;
    
    public GetNumberStatOfPlayer(String playerName, double value) {
        this.playerName = playerName;
        this.value = value;
    }
}


