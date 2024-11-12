package dev.florian.linz.captainsmode.player;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "players")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {

    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    private String puuid;
    
    private Integer playerOrder;
    
    public Player(String name) {
        this.name = name;
    }
    
}
