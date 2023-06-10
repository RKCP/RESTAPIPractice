package com.raphael.WeatherAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Location(
        @JsonProperty("name") String location,
        @JsonProperty("lat") Double latitude,
        @JsonProperty("lon") Double longitude) {}
