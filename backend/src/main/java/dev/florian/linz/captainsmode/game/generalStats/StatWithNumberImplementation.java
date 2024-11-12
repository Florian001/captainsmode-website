package dev.florian.linz.captainsmode.game.generalStats;

import dev.florian.linz.captainsmode.game.GameRepository;

public record StatWithNumberImplementation(String name, Number number) implements GameRepository.StatWithNumber {
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Number getNumber() {
        return number;
    }
}
