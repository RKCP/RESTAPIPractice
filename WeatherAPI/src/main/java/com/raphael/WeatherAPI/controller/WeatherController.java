package com.raphael.WeatherAPI.controller;

import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.WeatherForecast;
import com.raphael.WeatherAPI.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class WeatherController {

    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @GetMapping("/weather/{location}")
    public String getCurrentWeatherFromLocation(@PathVariable("location") String location, Model model) throws Exception {

        Optional<CurrentWeather> optionalCurrentWeather = service.getCurrentWeather(location);

        if (optionalCurrentWeather.isPresent()) {
            CurrentWeather currentWeather = optionalCurrentWeather.get();
            model.addAttribute("currentWeather", currentWeather);
            return "current-weather";
        } else {
            throw new Exception("Weather information not available for the specified location");
        }
    }

    @GetMapping("/forecast/{location}")
    public WeatherForecast getForecastFromLocation(@PathVariable("location") String location, Model model) throws Exception {

        List<WeatherForecast> optionalWeatherForecast = service.getWeatherForecast(location);

        if (!optionalWeatherForecast.isEmpty()) {
            WeatherForecast weatherForecast = optionalWeatherForecast.get(0);
            model.addAttribute("forecastWeather", weatherForecast);
            //return "forecast-weather";

            return weatherForecast;
        } else {
            throw new Exception("Weather information not available for the specified location");
        }
    }
}
