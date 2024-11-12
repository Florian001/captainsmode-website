package dev.florian.linz.captainsmode.player;


import dev.florian.linz.captainsmode.rest.error.BadRequestException;
import dev.florian.linz.captainsmode.rest.error.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository playerRepository;
    
    public List<Player> allPlayers() {
        
        return playerRepository.findAllPlayersExceptEnemy();
    }
    
    public Player getPlayerByName(String name) {
        Optional<Player> player = playerRepository.findPlayerByName(name);
        if (player.isPresent()) {
            return player.get();
        } else {
            throw new BadRequestException(ErrorCode.PLAYER_NOT_FOUND, "Player not found");
        }
    }
    
    public Player createPlayer(String name, String puuid) {
        Player player = new Player(name);
        player.setPuuid(puuid);
        Player insertedPlayer = playerRepository.save(player);
        return insertedPlayer;
    }
}
