package com.raphael.WeatherAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherForecast(String location,
                              String day,
                              int id,
                              @JsonProperty("temp") int temperature,
                              @JsonProperty("humidity") int humidity,
                              @JsonProperty("speed") Double windSpeed,
                              @JsonProperty("description") String description) {
}