package dev.florian.linz.captainsmode.game.generalStats;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
    @JsonSubTypes.Type(value = GetNumberStatResponse.class),
    @JsonSubTypes.Type(value = GetStringStatsResponse.class),
})
public sealed interface GetStatsResponse permits GetNumberStatResponse, GetStringStatsResponse {
}
