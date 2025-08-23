package dev.florian.linz.captainsmode.quiz;


import java.time.LocalDateTime;

public record PostChampionQuizResultRequest(Integer points, LocalDateTime dateTime) {
}
