package com.raphael.WeatherAPI.controller;

public class WeatherIconUtil {
    public static String getWeatherIconClass(int weatherId) {
        // Map weather IDs to corresponding Font Awesome icon classes
        if (weatherId == 800) {
            return "fas fa-sun";
        }

        switch (weatherId / 100) {
            case 2:
                return "fas fa-thunderstorm"; // Icon class for thunderstorm
            case 3:
                return "fas fa-cloud-drizzle"; // Icon class for drizzle
            case 5:
                return "fas fa-cloud-rain"; // Icon class for rain
            case 6:
                return "fas fa-cloud-snow"; // Icon class for snow
            case 7:
                return "fas fa-fog"; // Icon class for fog
            case 8:
                return "fas fa-cloud"; // Icon class for clouds
            default:
                return "fas fa-sun"; // Default icon class for other conditions
        }
    }
}
