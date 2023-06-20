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

    /**
     * Sets the root URL as our defined homepage
     *
     * @return the view name for thymeleaf to display the weather api home page
     */
    @GetMapping("/")
    public String weatherHomepage() {
        return "welcome-page";
    }

    /**
     * Retrieves the current weather for the specified location and adds it to the model.
     *
     * @param location the location for which to retrieve the current weather
     * @param model    the model to add the current weather object
     * @return the view name for displaying the current weather
     * @throws Exception if the weather information is not available
     */
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

    /**
     * Retrieves the weather forecast for the specified location and adds it to the model.
     *
     * @param location the location for which to retrieve the weather forecast
     * @param model    the model to add the list of weather forecasts
     * @return the view name for displaying the weather forecast
     * @throws Exception if the weather information is not available
     */
    @GetMapping("/forecast/{location}")
    public String getForecastFromLocation(@PathVariable("location") String location, Model model) throws Exception {

        List<WeatherForecast> weatherForecasts = service.getWeatherForecast(location);

        if (!weatherForecasts.isEmpty()) {
            model.addAttribute("forecastedWeather", weatherForecasts); // forecastedWeather is the variable we will use in HTML to access the properties
            return "forecast-weather";
        } else {
            throw new Exception("Weather information not available for the specified location");
        }
    }
}
