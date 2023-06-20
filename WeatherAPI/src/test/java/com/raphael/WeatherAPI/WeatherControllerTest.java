package com.raphael.WeatherAPI;

import com.raphael.WeatherAPI.controller.WeatherController;
import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.WeatherForecast;
import com.raphael.WeatherAPI.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

    @Mock
    private WeatherService weatherServiceMock;

    @InjectMocks
    private WeatherController weatherController;

    @Test
    void getCurrentWeatherFromLocation_WeatherAvailable_ReturnsCurrentWeatherPage() throws Exception {
        // Given
        String location = "London";
        CurrentWeather currentWeather = new CurrentWeather(location, 804, 20, 80, 10.0, "Cloudy");
        when(weatherServiceMock.getCurrentWeather(location)).thenReturn(Optional.of(currentWeather)); // mock that when the weatherService.getCurrentWeather("London") method is called, thenReturn our created currentWeather object
        Model model = new ExtendedModelMap(); // create a model that we need to pass to the controller

        // When
        String viewName = weatherController.getCurrentWeatherFromLocation(location, model); // this Controller method will reach into the weatherService and internally call the getCurrentWeather method... Instead of actually calling the real service method and going to the external API, we will pass what is returned from our mock instead. That is why we do @InjectMocks on our weatherController field, to inject the @Mock (our weatherService) into it, and use that mock instead of the real Service layer.

        // Then
        assertEquals("current-weather", viewName);
        assertEquals(currentWeather, model.getAttribute("currentWeather"));
    }

    @Test
    void getCurrentWeatherFromLocation_WeatherNotAvailable_ThrowsException() throws Exception {
        // Arrange
        String location = "Par1s";
        when(weatherServiceMock.getCurrentWeather(location)).thenReturn(Optional.empty());
        Model model = new ExtendedModelMap();

        // Act and Assert
        assertThrows(Exception.class, () -> weatherController.getCurrentWeatherFromLocation(location, model));
    }

    @Test
    void getWeatherForecastFromLocation_WeatherAvailable_ReturnsWeatherForecastPage() throws Exception {
        // Given
        String location = "London";
        WeatherForecast weatherForecast = new WeatherForecast("London", "Mon", 804, 20, 80, 10.0, "Cloudy");
        List<WeatherForecast> weatherForecastList = List.of(weatherForecast);
        when(weatherServiceMock.getWeatherForecast(location)).thenReturn(weatherForecastList); // mock that when the weatherService.getCurrentWeather("London") method is called, thenReturn our created currentWeather object
        Model model = new ExtendedModelMap(); // create a model that we need to pass to the controller

        // When
        String viewName = weatherController.getForecastFromLocation(location, model); // this Controller method will reach into the weatherService and internally call the getWeatherForecast method... Instead of actually calling the real service method and going to the external API, we will pass what is returned from our mock instead. That is why we do @InjectMocks on our weatherController field, to inject the @Mock (our weatherService) into it, and use that mock instead of the real Service layer.

        // Then
        assertEquals("forecast-weather", viewName);
        assertEquals(weatherForecastList, model.getAttribute("forecastedWeather"));
    }

    @Test
    void getWeatherHomepage_ReturnsWeatherHomepage() throws Exception {
        // Given
        String homepage = "welcome-page";

        // When
        String viewName = weatherController.weatherHomepage();

        // Then
        assertEquals(homepage, viewName);
    }
}