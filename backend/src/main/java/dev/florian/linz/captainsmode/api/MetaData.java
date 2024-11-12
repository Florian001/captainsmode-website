package dev.florian.linz.captainsmode.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MetaData(
    String matchId,
    String[] participants
) {}
