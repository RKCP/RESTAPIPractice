package com.raphael.WeatherAPI.controller;

import com.raphael.WeatherAPI.repository.WeatherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeatherController {

    private WeatherRepository repository;

    public WeatherController(WeatherRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/weather")
    public String getWeatherFromLocation(String location) {
        // get data from location (do logic in service class)

        // take long/lat from data and get weather data

        // map to Weather object

        // return in this method.

        return "hello";
    }
}
