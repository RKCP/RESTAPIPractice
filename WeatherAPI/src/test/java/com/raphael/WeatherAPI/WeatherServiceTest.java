package com.raphael.WeatherAPI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.Location;
import com.raphael.WeatherAPI.model.response.CurrentWeatherResponse;
import com.raphael.WeatherAPI.repository.WeatherRepository;
import com.raphael.WeatherAPI.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private ObjectMapper objectMapperMock;

    @Mock
    private WeatherRepository weatherRepositoryMock;

    @InjectMocks
    private WeatherService weatherService;

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Test
    void getCurrentWeather_ValidLocation_ReturnsCurrentWeather() {
        // Given
        String dummyGetWeatherJson = "{\"weather\":[{\"main\":\"Clear\"}],\"main\":{\"temp\":294.31,\"feels_like\":293.87,\"temp_min\":291.38,\"temp_max\":297.05,\"pressure\":1016,\"humidity\":53},\"wind\":{\"speed\":7.2,\"deg\":110}}";

        Map<String, Object> data = Map.of("name", "London", "lat", 51.5074, "lon", -0.1278);

        CurrentWeatherResponse currentWeatherResponse = new CurrentWeatherResponse(
                Map.of("temp", 294.31, "humidity", 53),
                Map.of("speed", 7.2),
                List.of(Map.of("id", 801, "description", "Few clouds"))
        );

        Location locationObject = new Location("London", new Location.Coordinates(51.50853, -0.12574));

        try {
            Mockito.when(weatherRepositoryMock.findById(anyString()))
                    .thenReturn(Optional.of(locationObject));

            Mockito.when(restTemplateMock.getForObject(anyString(), Mockito.eq(String.class)))
                    .thenReturn(dummyGetWeatherJson);

            Mockito.when(objectMapperMock.readValue(anyString(), any(TypeReference.class)))
                    .thenReturn(data);

            Mockito.when(objectMapperMock.convertValue(any(Map.class), eq(CurrentWeatherResponse.class)))
                    .thenReturn(currentWeatherResponse);

        } catch (Exception e) {
            logger.error("Error occurred while generating URI in test or deserializing the location data: {}", e.getMessage());
        }

        // When
        Optional<CurrentWeather> result = weatherService.getCurrentWeather("london");

        // Then
        Assertions.assertTrue(result.isPresent());
        CurrentWeather currentWeather = result.get();

        Assertions.assertEquals("London", currentWeather.location());
        Assertions.assertEquals(21, currentWeather.temperature());
        Assertions.assertEquals(53, currentWeather.humidity());
        Assertions.assertEquals(7.2, currentWeather.windSpeed());
        Assertions.assertEquals("Few clouds", currentWeather.description());
        Assertions.assertEquals(801, currentWeather.id());
        verify(restTemplateMock, times(1)).getForObject("https://api.openweathermap.org/data/2.5/weather?lat=51.50853&lon=-0.12574&appid=e30fcfa99c63f0e68d5d5a4e7bdd089a", String.class);
        verify(weatherRepositoryMock, times(1)).findById("London");
    }




//
//    @Test
//    void getCurrentWeather_InvalidLocation_ReturnsEmptyOptional() {
//        // Given
////        String location = "InvalidLocation";
////        when(weatherApiClient.getCurrentWeather(location)).thenReturn(Optional.empty());
////
////        // When
////        Optional<CurrentWeather> result = weatherService.getCurrentWeather(location);
////
////        // Then
////        Assertions.assertTrue(result.isEmpty());
////        verify(weatherApiClient, times(1)).getCurrentWeather(location);
////    }
////
////    @Test
////    void getWeatherForecast_ValidLocation_ReturnsWeatherForecastList() {
////        // Given
////        String location = "London";
////        WeatherForecastResponse response = new WeatherForecastResponse();
////        response.setList(List.of(
////                createForecastResponse(1622296800, 294.15, 80, 4.8, "Rain", 500),
////                createForecastResponse(1622383200, 296.15, 70, 3.2, "Cloudy", 802),
////                createForecastResponse(1622469600, 292.15, 85, 6.5, "Thunderstorm", 200)
////        ));
////        when(weatherApiClient.getWeatherForecast(location)).thenReturn(response);
////
////        // When
////        List<WeatherForecast> result = weatherService.getWeatherForecast(location);
////
////        // Then
////        Assertions.assertEquals(3, result.size());
////
////        WeatherForecast forecast1 = result.get(0);
////        Assertions.assertEquals("London", forecast1.getLocation());
////        Assertions.assertEquals("Mon", forecast1.getDayOfWeek());
////        Assertions.assertEquals(21, forecast1.getTemperature());
////        Assertions.assertEquals(80, forecast1.getHumidity());
////        Assertions.assertEquals(4.8, forecast1.getWindSpeed());
////        Assertions.assertEquals("Rain", forecast1.getDescription());
////        Assertions.assertEquals(500, forecast1.getId());
//
//        // ... assertions for the other forecast objects
//    }
//
//    private Map<String, Object> createForecastResponse(long date, double temp, int humidity, double windSpeed, String description, int id) {
//        Map<String, Object> forecast = Map.of(
//                "dt", date,
//                "main", Map.of("temp", temp, "humidity", humidity),
//                "wind", Map.of("speed", windSpeed),
//                "weather", List.of(Map.of("description", description, "id", id))
//        );
//        return forecast;
//    }
}
























//    @Test
//    void getCurrentWeather_ValidLocation_ReturnsCurrentWeather() {
//        // Given
//        String location = "london";
////
////        Map<String, Object> main = Map.of("temp", 293.15, "humidity", 75);
////        Map<String, Object> wind = Map.of("speed", 5.2);
////        List<Map<String, Object>> weather = List.of(Map.of("description", "Cloudy", "id", 801));
////
////        CurrentWeatherResponse generatedResponse = new CurrentWeatherResponse(main, wind, weather);
//
//        CurrentWeather generatedCurrentWeather = new CurrentWeather(location, 801, 20, 75, 5.2, "Cloudy");
//        Location generatedLocation = new Location(51.5073219, -0.1276474);
//
//        when(weatherService.getCurrentWeather(location)).thenReturn(Optional.of(generatedCurrentWeather));
//
////        when(restTemplateMock.getForObject("https://dummyurl.com", String.class)).thenReturn(Map.of().toString()); // restTemplate returns a String as its response (JSON String) so we just return an emptyMap as a String, since we will mock the following behavior of the objectMapper to get our CurrentWeatherResponse object.
////
////        try {
////            when(objectMapper.readValue("String returned by restTemplate", new TypeReference<>() {})).thenReturn(Map.of("dummyData", "dummyObject")); // we need to mock the objectMapper.readValue since it will be called in the service method, after the restTemplate is called.
////        } catch (JsonProcessingException e) {
////            logger.error("Error occurred while retrieving or deserializing the location data: {}", e.getMessage());
////        }
////
////        when(objectMapper.convertValue(Map.of("dummyData", "dummyObject"), CurrentWeatherResponse.class)).thenReturn(generatedResponse);
//
//        // When
//        Optional<CurrentWeather> result = weatherService.getCurrentWeather(location);
//
//        // Then
//        Assertions.assertTrue(result.isPresent());
//        CurrentWeather currentWeather = result.get();
//
//        Assertions.assertEquals("London", currentWeather.location());
//        Assertions.assertEquals(20, currentWeather.temperature());
//        Assertions.assertEquals(75, currentWeather.humidity());
//        Assertions.assertEquals(5.2, currentWeather.windSpeed());
//        Assertions.assertEquals("Cloudy", currentWeather.description());
//        Assertions.assertEquals(801, currentWeather.id());
//        verify(restTemplateMock, times(1)).getForObject("https://dummyurl.com", String.class);
//        verify(weatherService, times(1)).getCurrentWeather("London");
//    }