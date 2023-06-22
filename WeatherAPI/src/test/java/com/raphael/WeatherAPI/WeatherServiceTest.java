package com.raphael.WeatherAPI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.WeatherForecast;
import com.raphael.WeatherAPI.model.Location;
import com.raphael.WeatherAPI.model.response.CurrentWeatherResponse;
import com.raphael.WeatherAPI.model.response.WeatherForecastResponse;
import com.raphael.WeatherAPI.repository.WeatherRepository;
import com.raphael.WeatherAPI.service.WeatherService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
        Location locationObject = new Location("London", new Location.Coordinates(51.50853, -0.12574));

        String dummyGetWeatherJson = "{\"weather\":[{\"main\":\"Clear\"}],\"main\":{\"temp\":294.31,\"feels_like\":293.87,\"temp_min\":291.38,\"temp_max\":297.05,\"pressure\":1016,\"humidity\":53},\"wind\":{\"speed\":7.2,\"deg\":110}}";

        Map<String, Object> data = Map.of("name", "London", "lat", 51.5074, "lon", -0.1278);

        CurrentWeatherResponse currentWeatherResponse = new CurrentWeatherResponse(
                Map.of("temp", 294.31, "humidity", 53),
                Map.of("speed", 7.2),
                List.of(Map.of("id", 801, "description", "Few clouds"))
        );

        when(weatherRepositoryMock.findById(anyString()))
                .thenReturn(Optional.of(locationObject));

        when(restTemplateMock.getForObject(anyString(), eq(String.class)))
                .thenReturn(dummyGetWeatherJson);

        when(objectMapperMock.convertValue(any(Map.class), eq(CurrentWeatherResponse.class)))
                .thenReturn(currentWeatherResponse);

        try {
            when(objectMapperMock.readValue(anyString(), any(TypeReference.class)))
                    .thenReturn(data);
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


    @Test
    void getCurrentWeather_InvalidLocation_ReturnsEmptyOptional() {
        // Given
        String location = "Invalid Location";

        when(weatherRepositoryMock.findById(anyString()))
                .thenReturn(Optional.empty());

        // When
        Optional<CurrentWeather> result = weatherService.getCurrentWeather(location);

        // Then
        Assertions.assertTrue(result.isEmpty());
        verify(weatherRepositoryMock, times(1)).findById(location);
    }


    @Test
    void getWeatherForecast_ValidLocation_ReturnsWeatherForecastList() {
        // Given
        Location locationObject = new Location("London", new Location.Coordinates(51.50853, -0.12574));

        String dummyGetWeatherJson = "{\"cod\":\"200\",\"message\":0,\"cnt\":40,\"list\":[{\"dt\":1687024800,\"main\":{\"temp\":297.24,\"feels_like\":297.11,\"temp_min\":294.58,\"temp_max\":297.24,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":1009,\"humidity\":54,\"temp_kf\":2.66},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":{\"all\":47},\"wind\":{\"speed\":3.9,\"deg\":89,\"gust\":5.77},\"visibility\":10000,\"pop\":0.26,\"rain\":{\"3h\":0.66},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-17 18:00:00\"},{\"dt\":1687035600,\"main\":{\"temp\":292.96,\"feels_like\":293.01,\"temp_min\":290.15,\"temp_max\":292.96,\"pressure\":1012,\"sea_level\":1012,\"grnd_level\":1009,\"humidity\":77,\"temp_kf\":2.81},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"clouds\":{\"all\":73},\"wind\":{\"speed\":2.36,\"deg\":74,\"gust\":5.28},\"visibility\":10000,\"pop\":0.5,\"rain\":{\"3h\":2.71},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-17 21:00:00\"},{\"dt\":1687046400,\"main\":{\"temp\":289.85,\"feels_like\":289.98,\"temp_min\":289.85,\"temp_max\":289.85,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":1008,\"humidity\":92,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":1.35,\"deg\":104,\"gust\":2.03},\"visibility\":10000,\"pop\":0.51,\"rain\":{\"3h\":0.17},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-18 00:00:00\"},{\"dt\":1687057200,\"main\":{\"temp\":289.52,\"feels_like\":289.61,\"temp_min\":289.52,\"temp_max\":289.52,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":1007,\"humidity\":92,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":0.87,\"deg\":188,\"gust\":0.95},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-18 03:00:00\"},{\"dt\":1687068000,\"main\":{\"temp\":290.96,\"feels_like\":290.99,\"temp_min\":290.96,\"temp_max\":290.96,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":1007,\"humidity\":84,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":98},\"wind\":{\"speed\":0.52,\"deg\":243,\"gust\":0.9},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-18 06:00:00\"},{\"dt\":1687078800,\"main\":{\"temp\":295.31,\"feels_like\":295.15,\"temp_min\":295.31,\"temp_max\":295.31,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":1007,\"humidity\":60,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":1.13,\"deg\":127,\"gust\":1.56},\"visibility\":10000,\"pop\":0.34,\"rain\":{\"3h\":0.12},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-18 09:00:00\"},{\"dt\":1687089600,\"main\":{\"temp\":299.06,\"feels_like\":299.01,\"temp_min\":299.06,\"temp_max\":299.06,\"pressure\":1008,\"sea_level\":1008,\"grnd_level\":1006,\"humidity\":50,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":2.12,\"deg\":165,\"gust\":3.36},\"visibility\":10000,\"pop\":0.34,\"rain\":{\"3h\":0.18},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-18 12:00:00\"},{\"dt\":1687100400,\"main\":{\"temp\":295.01,\"feels_like\":295.31,\"temp_min\":295.01,\"temp_max\":295.01,\"pressure\":1008,\"sea_level\":1008,\"grnd_level\":1005,\"humidity\":79,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":3.15,\"deg\":212,\"gust\":5.48},\"visibility\":10000,\"pop\":0.81,\"rain\":{\"3h\":2.45},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-18 15:00:00\"},{\"dt\":1687111200,\"main\":{\"temp\":293.03,\"feels_like\":293.03,\"temp_min\":293.03,\"temp_max\":293.03,\"pressure\":1008,\"sea_level\":1008,\"grnd_level\":1005,\"humidity\":75,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":4.17,\"deg\":265,\"gust\":5.73},\"visibility\":10000,\"pop\":0.83,\"rain\":{\"3h\":0.52},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-18 18:00:00\"},{\"dt\":1687122000,\"main\":{\"temp\":289.33,\"feels_like\":289.43,\"temp_min\":289.33,\"temp_max\":289.33,\"pressure\":1009,\"sea_level\":1009,\"grnd_level\":1007,\"humidity\":93,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"clouds\":{\"all\":90},\"wind\":{\"speed\":2.82,\"deg\":277,\"gust\":5.39},\"visibility\":9541,\"pop\":0.96,\"rain\":{\"3h\":1.73},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-18 21:00:00\"},{\"dt\":1687132800,\"main\":{\"temp\":288.07,\"feels_like\":288.1,\"temp_min\":288.07,\"temp_max\":288.07,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":1007,\"humidity\":95,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"clouds\":{\"all\":75},\"wind\":{\"speed\":2.61,\"deg\":232,\"gust\":7.42},\"visibility\":10000,\"pop\":1,\"rain\":{\"3h\":0.48},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-19 00:00:00\"},{\"dt\":1687143600,\"main\":{\"temp\":286.76,\"feels_like\":286.66,\"temp_min\":286.76,\"temp_max\":286.76,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":1007,\"humidity\":95,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"clouds\":{\"all\":9},\"wind\":{\"speed\":2.58,\"deg\":226,\"gust\":8.12},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-19 03:00:00\"},{\"dt\":1687154400,\"main\":{\"temp\":287.94,\"feels_like\":287.77,\"temp_min\":287.94,\"temp_max\":287.94,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":1008,\"humidity\":88,\"temp_kf\":0},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"clouds\":{\"all\":24},\"wind\":{\"speed\":3.13,\"deg\":235,\"gust\":7.51},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-19 06:00:00\"},{\"dt\":1687165200,\"main\":{\"temp\":293.63,\"feels_like\":293.25,\"temp_min\":293.63,\"temp_max\":293.63,\"pressure\":1012,\"sea_level\":1012,\"grnd_level\":1009,\"humidity\":58,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":{\"all\":50},\"wind\":{\"speed\":3.61,\"deg\":223,\"gust\":5.23},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-19 09:00:00\"},{\"dt\":1687176000,\"main\":{\"temp\":297.39,\"feels_like\":296.94,\"temp_min\":297.39,\"temp_max\":297.39,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":1008,\"humidity\":41,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":74},\"wind\":{\"speed\":4.2,\"deg\":204,\"gust\":5.11},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-19 12:00:00\"},{\"dt\":1687186800,\"main\":{\"temp\":297.41,\"feels_like\":296.96,\"temp_min\":297.41,\"temp_max\":297.41,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":1008,\"humidity\":41,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":4.25,\"deg\":190,\"gust\":3.97},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-19 15:00:00\"},{\"dt\":1687197600,\"main\":{\"temp\":293.83,\"feels_like\":293.42,\"temp_min\":293.83,\"temp_max\":293.83,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":1008,\"humidity\":56,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":4.17,\"deg\":200,\"gust\":5.66},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-19 18:00:00\"},{\"dt\":1687208400,\"main\":{\"temp\":291.99,\"feels_like\":291.37,\"temp_min\":291.99,\"temp_max\":291.99,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":1008,\"humidity\":55,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":1.19,\"deg\":232,\"gust\":1.99},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-19 21:00:00\"},{\"dt\":1687219200,\"main\":{\"temp\":291.6,\"feels_like\":290.96,\"temp_min\":291.6,\"temp_max\":291.6,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":1007,\"humidity\":56,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":1.15,\"deg\":69,\"gust\":1.86},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-20 00:00:00\"},{\"dt\":1687230000,\"main\":{\"temp\":288.61,\"feels_like\":288.51,\"temp_min\":288.61,\"temp_max\":288.61,\"pressure\":1009,\"sea_level\":1009,\"grnd_level\":1006,\"humidity\":88,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":2.55,\"deg\":23,\"gust\":6.13},\"visibility\":10000,\"pop\":0.91,\"rain\":{\"3h\":1.34},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-20 03:00:00\"},{\"dt\":1687240800,\"main\":{\"temp\":287.91,\"feels_like\":288,\"temp_min\":287.91,\"temp_max\":287.91,\"pressure\":1007,\"sea_level\":1007,\"grnd_level\":1004,\"humidity\":98,\"temp_kf\":0},\"weather\":[{\"id\":502,\"main\":\"Rain\",\"description\":\"heavy intensity rain\",\"icon\":\"10d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":3.02,\"deg\":18,\"gust\":7.06},\"visibility\":2082,\"pop\":1,\"rain\":{\"3h\":17.41},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-20 06:00:00\"},{\"dt\":1687251600,\"main\":{\"temp\":287.98,\"feels_like\":288.03,\"temp_min\":287.98,\"temp_max\":287.98,\"pressure\":1008,\"sea_level\":1008,\"grnd_level\":1005,\"humidity\":96,\"temp_kf\":0},\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":2.75,\"deg\":357,\"gust\":5.65},\"visibility\":10000,\"pop\":0.77,\"rain\":{\"3h\":5.89},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-20 09:00:00\"},{\"dt\":1687262400,\"main\":{\"temp\":289.31,\"feels_like\":289.28,\"temp_min\":289.31,\"temp_max\":289.31,\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":1007,\"humidity\":88,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":2.66,\"deg\":317,\"gust\":4.05},\"visibility\":10000,\"pop\":0.73,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-20 12:00:00\"},{\"dt\":1687273200,\"main\":{\"temp\":289.69,\"feels_like\":289.57,\"temp_min\":289.69,\"temp_max\":289.69,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":1008,\"humidity\":83,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":{\"all\":100},\"wind\":{\"speed\":2.36,\"deg\":284,\"gust\":3.63},\"visibility\":10000,\"pop\":0.24,\"rain\":{\"3h\":0.12},\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-20 15:00:00\"},{\"dt\":1687284000,\"main\":{\"temp\":291.85,\"feels_like\":291.71,\"temp_min\":291.85,\"temp_max\":291.85,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":1008,\"humidity\":74,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":87},\"wind\":{\"speed\":1.9,\"deg\":253,\"gust\":3.78},\"visibility\":10000,\"pop\":0.16,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-20 18:00:00\"},{\"dt\":1687294800,\"main\":{\"temp\":289.26,\"feels_like\":289.28,\"temp_min\":289.26,\"temp_max\":289.26,\"pressure\":1012,\"sea_level\":1012,\"grnd_level\":1009,\"humidity\":90,\"temp_kf\":0},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02n\"}],\"clouds\":{\"all\":15},\"wind\":{\"speed\":1.94,\"deg\":208,\"gust\":3.64},\"visibility\":10000,\"pop\":0.14,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-20 21:00:00\"},{\"dt\":1687305600,\"main\":{\"temp\":287.87,\"feels_like\":287.85,\"temp_min\":287.87,\"temp_max\":287.87,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":1010,\"humidity\":94,\"temp_kf\":0},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"clouds\":{\"all\":7},\"wind\":{\"speed\":2.29,\"deg\":210,\"gust\":7.33},\"visibility\":10000,\"pop\":0.34,\"rain\":{\"3h\":0.19},\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-21 00:00:00\"},{\"dt\":1687316400,\"main\":{\"temp\":286.62,\"feels_like\":286.45,\"temp_min\":286.62,\"temp_max\":286.62,\"pressure\":1013,\"sea_level\":1013,\"grnd_level\":1011,\"humidity\":93,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"clouds\":{\"all\":0},\"wind\":{\"speed\":2.36,\"deg\":221,\"gust\":8.56},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-21 03:00:00\"},{\"dt\":1687327200,\"main\":{\"temp\":288.51,\"feels_like\":288.35,\"temp_min\":288.51,\"temp_max\":288.51,\"pressure\":1015,\"sea_level\":1015,\"grnd_level\":1012,\"humidity\":86,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":1},\"wind\":{\"speed\":3.03,\"deg\":229,\"gust\":8.42},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-21 06:00:00\"},{\"dt\":1687338000,\"main\":{\"temp\":293.3,\"feels_like\":293.02,\"temp_min\":293.3,\"temp_max\":293.3,\"pressure\":1015,\"sea_level\":1015,\"grnd_level\":1013,\"humidity\":63,\"temp_kf\":0},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":{\"all\":1},\"wind\":{\"speed\":4.06,\"deg\":225,\"gust\":6.43},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-21 09:00:00\"},{\"dt\":1687348800,\"main\":{\"temp\":296.56,\"feels_like\":296.18,\"temp_min\":296.56,\"temp_max\":296.56,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":1013,\"humidity\":47,\"temp_kf\":0},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"clouds\":{\"all\":14},\"wind\":{\"speed\":4.92,\"deg\":216,\"gust\":6.53},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-21 12:00:00\"},{\"dt\":1687359600,\"main\":{\"temp\":296.9,\"feels_like\":296.51,\"temp_min\":296.9,\"temp_max\":296.9,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":1013,\"humidity\":45,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":{\"all\":31},\"wind\":{\"speed\":4.74,\"deg\":212,\"gust\":5.68},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-21 15:00:00\"},{\"dt\":1687370400,\"main\":{\"temp\":294.87,\"feels_like\":294.51,\"temp_min\":294.87,\"temp_max\":294.87,\"pressure\":1016,\"sea_level\":1016,\"grnd_level\":1014,\"humidity\":54,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":{\"all\":37},\"wind\":{\"speed\":3.7,\"deg\":213,\"gust\":4.46},\"visibility\":10000,\"pop\":0.02,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-21 18:00:00\"},{\"dt\":1687381200,\"main\":{\"temp\":290.94,\"feels_like\":290.68,\"temp_min\":290.94,\"temp_max\":290.94,\"pressure\":1018,\"sea_level\":1018,\"grnd_level\":1015,\"humidity\":73,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":83},\"wind\":{\"speed\":2.13,\"deg\":214,\"gust\":5.53},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-21 21:00:00\"},{\"dt\":1687392000,\"main\":{\"temp\":289.23,\"feels_like\":289.03,\"temp_min\":289.23,\"temp_max\":289.23,\"pressure\":1019,\"sea_level\":1019,\"grnd_level\":1016,\"humidity\":82,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":70},\"wind\":{\"speed\":1.68,\"deg\":237,\"gust\":4.02},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-22 00:00:00\"},{\"dt\":1687402800,\"main\":{\"temp\":288.06,\"feels_like\":287.96,\"temp_min\":288.06,\"temp_max\":288.06,\"pressure\":1018,\"sea_level\":1018,\"grnd_level\":1016,\"humidity\":90,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"clouds\":{\"all\":69},\"wind\":{\"speed\":1.38,\"deg\":256,\"gust\":2.06},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-06-22 03:00:00\"},{\"dt\":1687413600,\"main\":{\"temp\":289.55,\"feels_like\":289.28,\"temp_min\":289.55,\"temp_max\":289.55,\"pressure\":1019,\"sea_level\":1019,\"grnd_level\":1016,\"humidity\":78,\"temp_kf\":0},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":76},\"wind\":{\"speed\":1.2,\"deg\":296,\"gust\":1.8},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-22 06:00:00\"},{\"dt\":1687424400,\"main\":{\"temp\":293.67,\"feels_like\":293.19,\"temp_min\":293.67,\"temp_max\":293.67,\"pressure\":1019,\"sea_level\":1019,\"grnd_level\":1017,\"humidity\":54,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":98},\"wind\":{\"speed\":1.81,\"deg\":293,\"gust\":2.06},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-22 09:00:00\"},{\"dt\":1687435200,\"main\":{\"temp\":296.97,\"feels_like\":296.56,\"temp_min\":296.97,\"temp_max\":296.97,\"pressure\":1019,\"sea_level\":1019,\"grnd_level\":1017,\"humidity\":44,\"temp_kf\":0},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":{\"all\":97},\"wind\":{\"speed\":2.05,\"deg\":285,\"gust\":2.08},\"visibility\":10000,\"pop\":0,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-22 12:00:00\"},{\"dt\":1687446000,\"main\":{\"temp\":298.23,\"feels_like\":297.81,\"temp_min\":298.23,\"temp_max\":298.23,\"pressure\":1018,\"sea_level\":1018,\"grnd_level\":1016,\"humidity\":39,\"temp_kf\":0},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":{\"all\":42},\"wind\":{\"speed\":3.04,\"deg\":294,\"gust\":2.12},\"visibility\":10000,\"pop\":0.12,\"sys\":{\"pod\":\"d\"},\"dt_txt\":\"2023-06-22 15:00:00\"}],\"city\":{\"id\":2643743,\"name\":\"London\",\"coord\":{\"lat\":51.5074,\"lon\":-0.1278},\"country\":\"GB\",\"population\":1000000,\"timezone\":3600,\"sunrise\":1686973362,\"sunset\":1687033202}}";

        // -- construct the map for our objectMapper.convertValue()
        Map<String, Object> weatherForecastDetailsMap = new HashMap<>();
        weatherForecastDetailsMap.put("dt", 1687024800);
        Map<String, Object> mainMap = Map.ofEntries(
                Map.entry("temp", 297.24),
                Map.entry("feels_like", 297.11),
                Map.entry("temp_min", 294.58),
                Map.entry("temp_max", 297.24),
                Map.entry("pressure", 1013),
                Map.entry("sea_level", 1013),
                Map.entry("grnd_level", 1009),
                Map.entry("humidity", 54),
                Map.entry("temp_kf", 2.66)
        );
        weatherForecastDetailsMap.put("main", mainMap);
        weatherForecastDetailsMap.put("wind", Map.of("speed", 3.9));
        weatherForecastDetailsMap.put("weather",  List.of(Map.of("id", 500, "description", "light rain")));

        WeatherForecastResponse weatherForecastResponse = new WeatherForecastResponse(
                weatherForecastDetailsMap.get("dt"),
                mainMap,
                Map.of("speed", 3.9),
                List.of(Map.of("id", 500, "description", "light rain")));


        when(weatherRepositoryMock.findById(anyString()))
                .thenReturn(Optional.of(locationObject));

        when(restTemplateMock.getForObject(anyString(), eq(String.class)))
                .thenReturn(dummyGetWeatherJson);

        when(objectMapperMock.convertValue(any(Object.class), any(TypeReference.class)))
                .thenReturn(weatherForecastDetailsMap);

        when(objectMapperMock.convertValue(any(Map.class), eq(WeatherForecastResponse.class)))
                .thenReturn(weatherForecastResponse);

        try {
            // Parse the JSON string
            JSONObject json = new JSONObject(dummyGetWeatherJson);
            List<JSONObject> convertedList = List.of();

            try {
                JSONArray jsonArray = json.getJSONArray("list");
                List<JSONObject> jsonObjectList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    jsonObjectList.add(jsonObject);
                }
                convertedList = jsonObjectList;
            } catch (JSONException e) {
                logger.error("Error converting jsonArray to normal List in WeatherServiceTest");
            }

            // Extract the desired data from the JSON and convert it to a Map
            Map<String, Object> responseMap = Map.of(
                    "cod", json.getString("cod"),
                    "message", json.getInt("message"),
                    "cnt", json.getInt("cnt"),
                    "list", convertedList
            );

            when(objectMapperMock.readValue(anyString(), any(TypeReference.class)))
                    .thenReturn(responseMap);
        } catch (Exception e) {
            logger.error("Error occurred while generating URI in test or deserializing the location data: {}", e.getMessage());
        }

        // When
        List<WeatherForecast> result = weatherService.getWeatherForecast("london");

        // Then
        Assertions.assertEquals(1, result.size());

        WeatherForecast forecast = result.get(0);
        Assertions.assertEquals("London", forecast.location());
        Assertions.assertEquals("Sun", forecast.day());
        Assertions.assertEquals(24, forecast.temperature());
        Assertions.assertEquals(54, forecast.humidity());
        Assertions.assertEquals(3.9, forecast.windSpeed());
        Assertions.assertEquals("light rain", forecast.description());
        Assertions.assertEquals(500, forecast.id());
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


    @Test
    void getWeatherForecast_InvalidLocation_ReturnsEmptyArrayList() {
        // Given
        String location = "Invalid Location";

        when(weatherRepositoryMock.findById(anyString()))
                .thenReturn(Optional.empty());

        // When
        Optional<CurrentWeather> result = weatherService.getCurrentWeather(location);

        // Then
        Assertions.assertTrue(result.isEmpty());
        verify(weatherRepositoryMock, times(1)).findById(location);
    }
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