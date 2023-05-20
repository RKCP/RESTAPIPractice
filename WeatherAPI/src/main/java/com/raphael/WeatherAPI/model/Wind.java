package com.raphael.WeatherAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Wind(Double speed) {
}
