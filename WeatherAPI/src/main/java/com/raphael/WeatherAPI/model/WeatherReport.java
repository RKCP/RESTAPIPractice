package com.raphael.WeatherAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherReport(WeatherDescription weatherDescription, WeatherMain weatherMain, Wind wind) {
}
