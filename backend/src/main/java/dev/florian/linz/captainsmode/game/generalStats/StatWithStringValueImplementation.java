package dev.florian.linz.captainsmode.game.generalStats;

import dev.florian.linz.captainsmode.game.GameRepository;
import java.math.BigDecimal;
import java.sql.Timestamp;

public record StatWithStringValueImplementation(String name, String stringValue) implements GameRepository.StatWithStringValue {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }

    public StatWithStringValueImplementation(String name, BigDecimal duration) {
        this(name, mapBigIntToTimeString(duration));
    }
    
    private static String mapBigIntToTimeString(BigDecimal duration) {
        int totalSeconds = duration.intValue();
        int minutes = totalSeconds / 60;
        int remainingSeconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
