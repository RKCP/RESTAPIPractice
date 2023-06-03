package com.raphael.WeatherAPI;

import com.raphael.WeatherAPI.client.WeatherApiClient;
import com.raphael.WeatherAPI.client.dto.CurrentWeatherResponse;
import com.raphael.WeatherAPI.client.dto.WeatherForecastResponse;
import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.WeatherForecast;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @Mock
    private WeatherApiClient weatherApiClient;

    private WeatherService weatherService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        weatherService = new WeatherService(weatherApiClient);
    }

    @Test
    void getCurrentWeather_ValidLocation_ReturnsCurrentWeather() {
        // Given
        String location = "London";
        CurrentWeatherResponse response = new CurrentWeatherResponse();
        response.setMain(Map.of("temp", 293.15, "humidity", 75));
        response.setWind(Map.of("speed", 5.2));
        response.setWeather(List.of(Map.of("description", "Cloudy", "id", 801)));
        when(weatherApiClient.getCurrentWeather(location)).thenReturn(Optional.of(response));

        // When
        Optional<CurrentWeather> result = weatherService.getCurrentWeather(location);

        // Then
        Assertions.assertTrue(result.isPresent());
        CurrentWeather currentWeather = result.get();
        Assertions.assertEquals("London", currentWeather.getCityName());
        Assertions.assertEquals(20, currentWeather.getTemperature());
        Assertions.assertEquals(75, currentWeather.getHumidity());
        Assertions.assertEquals(5.2, currentWeather.getWindSpeed());
        Assertions.assertEquals("Cloudy", currentWeather.getDescription());
        Assertions.assertEquals(801, currentWeather.getId());
        verify(weatherApiClient, times(1)).getCurrentWeather(location);
    }

    @Test
    void getCurrentWeather_InvalidLocation_ReturnsEmptyOptional() {
        // Given
        String location = "InvalidLocation";
        when(weatherApiClient.getCurrentWeather(location)).thenReturn(Optional.empty());

        // When
        Optional<CurrentWeather> result = weatherService.getCurrentWeather(location);

        // Then
        Assertions.assertTrue(result.isEmpty());
        verify(weatherApiClient, times(1)).getCurrentWeather(location);
    }

    @Test
    void getWeatherForecast_ValidLocation_ReturnsWeatherForecastList() {
        // Given
        String location = "London";
        WeatherForecastResponse response = new WeatherForecastResponse();
        response.setList(List.of(
                createForecastResponse(1622296800, 294.15, 80, 4.8, "Rain", 500),
                createForecastResponse(1622383200, 296.15, 70, 3.2, "Cloudy", 802),
                createForecastResponse(1622469600, 292.15, 85, 6.5, "Thunderstorm", 200)
        ));
        when(weatherApiClient.getWeatherForecast(location)).thenReturn(response);

        // When
        List<WeatherForecast> result = weatherService.getWeatherForecast(location);

        // Then
        Assertions.assertEquals(3, result.size());

        WeatherForecast forecast1 = result.get(0);
        Assertions.assertEquals("London", forecast1.getLocation());
        Assertions.assertEquals("Mon", forecast1.getDayOfWeek());
        Assertions.assertEquals(21, forecast1.getTemperature());
        Assertions.assertEquals(80, forecast1.getHumidity());
        Assertions.assertEquals(4.8, forecast1.getWindSpeed());
        Assertions.assertEquals("Rain", forecast1.getDescription());
        Assertions.assertEquals(500, forecast1.getId());

        // ... assertions for the other forecast objects
    }

    private Map<String, Object> createForecastResponse(long date, double temp, int humidity, double windSpeed, String description, int id) {
        Map<String, Object> forecast = Map.of(
                "dt", date,
                "main", Map.of("temp", temp, "humidity", humidity),
                "wind", Map.of("speed", windSpeed),
                "weather", List.of(Map.of("description", description, "id", id))
        );
        return forecast;
    }
}


// need to mock the API in this layer.