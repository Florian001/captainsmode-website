package dev.florian.linz.captainsmode.utils;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseService {
    
    @Value("${lol.api.key}")
    protected String apiKey;

    public static final String BASE_URL = "https://europe.api.riotgames.com";

}
