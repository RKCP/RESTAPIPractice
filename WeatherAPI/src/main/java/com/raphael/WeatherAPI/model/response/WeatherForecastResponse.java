package com.raphael.WeatherAPI.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherForecastResponse(@JsonProperty("dt") Object date,
                                      @JsonProperty("main") Map<String, Object> main,
                                      @JsonProperty("wind") Map<String, Object> wind,
                                      @JsonProperty("weather") List<Map<String, Object>> weather) {
}
