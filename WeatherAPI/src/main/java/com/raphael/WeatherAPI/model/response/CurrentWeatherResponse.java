package com.raphael.WeatherAPI.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeatherResponse(@JsonProperty("temp") Double temperature,
                                     @JsonProperty("humidity") int humidity,
                                     @JsonProperty("speed") Double windSpeed,
                                     @JsonProperty("description") String description) {
}

