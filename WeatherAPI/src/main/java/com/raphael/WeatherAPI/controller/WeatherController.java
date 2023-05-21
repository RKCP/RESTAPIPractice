package com.raphael.WeatherAPI.controller;

import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.WeatherDescription;
import com.raphael.WeatherAPI.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WeatherController {

    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @GetMapping("/weather/{location}")
    public CurrentWeather getCurrentWeatherFromLocation(@PathVariable("location") String location) throws Exception {

        return service.getCurrentWeather(location);
    }
}
