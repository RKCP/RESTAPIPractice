package com.raphael.WeatherAPI.model;

public record CurrentWeather(Double temperature, int humidity, Double windSpeed, String description) {
}
