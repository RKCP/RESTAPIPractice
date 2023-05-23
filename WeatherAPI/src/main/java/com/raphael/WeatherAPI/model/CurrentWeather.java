package com.raphael.WeatherAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeather(String location,
                             @JsonProperty("temp") int temperature,
                             @JsonProperty("humidity") int humidity,
                             @JsonProperty("speed") Double windSpeed,
                             @JsonProperty("description") String description) {
}
