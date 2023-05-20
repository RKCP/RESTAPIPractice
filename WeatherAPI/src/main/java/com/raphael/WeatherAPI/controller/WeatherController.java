package com.raphael.WeatherAPI.controller;

import com.raphael.WeatherAPI.model.WeatherDescription;
import com.raphael.WeatherAPI.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherController {

    private WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @GetMapping("/weather")
    public WeatherDescription getWeatherFromLocation(String location) throws Exception {

        return service.getCurrentWeather(location);
    }
}
