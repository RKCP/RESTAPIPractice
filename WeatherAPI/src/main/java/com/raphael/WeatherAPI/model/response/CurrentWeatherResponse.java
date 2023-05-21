package com.raphael.WeatherAPI.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeatherResponse(@JsonProperty("main") Map<String, Object> main,
                                     @JsonProperty("wind") Map<String, Object> wind,
                                     @JsonProperty("weather") List<Map<String, Object>> weather) {
}

