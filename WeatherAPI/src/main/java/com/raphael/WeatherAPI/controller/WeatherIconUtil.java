package com.raphael.WeatherAPI.controller;

public class WeatherIconUtil {
    public static String getWeatherIconClass(String weatherDescription) {
        // Map weather descriptions to corresponding Font Awesome icon classes
        if (weatherDescription.equalsIgnoreCase("cloudy")) {
            return "fas fa-cloud";
        } else if (weatherDescription.equalsIgnoreCase("rainy")) {
            return "fas fa-cloud-rain";
        } else if (weatherDescription.equalsIgnoreCase("sunny")) {
            return "fas fa-sun";
        }
        // Add more mappings as needed

        // Default icon class
        return "fas fa-question"; // or any other default icon class
    }
}
