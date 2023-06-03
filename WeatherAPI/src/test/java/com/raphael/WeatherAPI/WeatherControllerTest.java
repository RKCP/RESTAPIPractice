package com.raphael.WeatherAPI;

import com.raphael.WeatherAPI.controller.WeatherController;
import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

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
        CurrentWeather currentWeatherMock = new CurrentWeather(location, 804, 20, 80, 10.0, "Cloudy");
        when(weatherServiceMock.getCurrentWeather(location)).thenReturn(Optional.of(currentWeatherMock)); // mock that when the weatherService.getCurrentWeather("London") method is called, thenReturn our created currentWeather object
        Model model = new ExtendedModelMap(); // create a model that we need to pass to the controller

        // When
        String viewName = weatherController.getCurrentWeatherFromLocation(location, model); // this Controller method will reach into the weatherService and internally call the getCurrentWeather method... Instead of actually calling the real service method and going to the external API, we will pass what is returned from our mock instead. That is why we do @InjectMocks on our weatherController field, to inject the @Mock (our weatherService) into it, and use that mock instead of the real Service layer.

        // Then
        assertEquals("current-weather", viewName);
        assertEquals(currentWeatherMock, model.getAttribute("currentWeather"));
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

    // Additional tests for other methods in WeatherController
}


// Overall, this code sets up a test scenario where the WeatherController object is being tested.
// The WeatherService dependency of the WeatherController is mocked using the @Mock annotation, and then injected into the WeatherController using the @InjectMocks annotation.
// This allows for isolated testing of the WeatherController without relying on the actual implementation of the WeatherService.