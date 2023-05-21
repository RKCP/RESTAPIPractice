package com.raphael.WeatherAPI.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.Location;
import com.raphael.WeatherAPI.model.WeatherReport;
import com.raphael.WeatherAPI.model.response.CurrentWeatherResponse;
import com.raphael.WeatherAPI.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WeatherService {

    private final String API_KEY = "cfe749fee8e119f7b17df5d7b4ed3301";

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final WeatherRepository weatherRepository;

    public WeatherService(RestTemplate restTemplate, WeatherRepository weatherRepository) {
        this.restTemplate = restTemplate;
        this.weatherRepository = weatherRepository;
    }


    /**
     * Gets the cityName, calls an internal method to get the lat/lon, and produces the current weather for the given location
     * @param cityName is the name of the city we are searching the weather for
     * @return the current weather details for the given location
     */
    public Optional<CurrentWeather> getCurrentWeather(String cityName) {

        Optional<Location> optionalLocation = getLatLonFromLocation(cityName);
        String baseUri = "https://api.openweathermap.org/data/2.5/weather";

        if (optionalLocation.isPresent()) {

            Location actualLocation = optionalLocation.get();

            try {
                URI uri = new URI(baseUri + "?lat=" + actualLocation.latitude() + "&lon=" + actualLocation.longitude() + "&appid=" + API_KEY);

                // Get the JSON response from the OpenWeather API. Get it as a string since objectMapper can handle the rest
                String response = restTemplate.getForObject(uri, String.class);

                // Deserialize the JSON array into a list of location maps, since we are receiving an array from OpenWeather API
                Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>(){});

                if (!responseMap.isEmpty()) {
                    // Use ObjectMapper to map the required fields from the JSON into our CurrentWeatherResponse object. Adding this object as an additional Layer due to the nested nature of the JSON response
                    CurrentWeatherResponse currentWeatherResponse = objectMapper.convertValue(responseMap, CurrentWeatherResponse.class);

                    // Extract the fields we need for the CurrentWeather object from CurrentWeatherResponse
                    Double temperature = (Double) currentWeatherResponse.main().get("temp");
                    int humidity = (Integer) currentWeatherResponse.main().get("humidity");
                    Double windSpeed = (Double) currentWeatherResponse.wind().get("speed");
                    String description = (String) currentWeatherResponse.weather().get(0).get("description");

                    // Create a new CurrentWeather object with the extracted values
                    CurrentWeather currentWeather = new CurrentWeather(temperature, humidity, windSpeed, description);

                    return Optional.of(currentWeather);
                }

            } catch (URISyntaxException e) {
                logger.error("Error occurred while building URL from cityName: {}", e.getMessage());
            } catch (Exception e) { // objectMapper.readValue possibly throwing two JSON exceptions. Have URISyntaxException for specific logging.
                logger.error("Error occurred while retrieving or deserializing the location data: {}", e.getMessage());
            }
        } else {
            logger.error("Location data is not available for the given city: {}", cityName);
        }
        return Optional.empty();
    }


    /**
     * Helper method that generates the lat/lon based on the given city name. This is required for OpenWeatherMap API, as it does not take a cityName and return relevant weather details
     * @param cityName is the name of the city we are searching the weather for
     * @return an optional Location object containing the lat/lon for the given cityName
     */
    public Optional<Location> getLatLonFromLocation(String cityName) {
        String baseUri = "https://api.openweathermap.org/geo/1.0/direct";

        try {
            URI uri = new URI(baseUri + "?q=" + cityName + "&limit=1&appid=" + API_KEY);

            // Get the JSON response from the OpenWeather API. Get it as a string since objectMapper can handle the rest
            String jsonResponse = restTemplate.getForObject(uri, String.class);

            // Deserialize the JSON array into a list of location maps, since we are receiving an array from OpenWeather API
            List<Map<String, Object>> responseList = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

            if (!responseList.isEmpty()) {
                Location location = objectMapper.convertValue(responseList.get(0), Location.class);
                return Optional.of(location);
            }

        } catch (URISyntaxException e) {
            logger.error("Error occurred while building URL from cityName: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error occurred while retrieving or deserializing the location data: {}", e.getMessage());
        }

        return Optional.empty();
    }
}




































