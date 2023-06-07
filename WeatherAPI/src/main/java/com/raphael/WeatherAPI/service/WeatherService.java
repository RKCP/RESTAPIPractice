package com.raphael.WeatherAPI.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.Location;
import com.raphael.WeatherAPI.model.WeatherForecast;
import com.raphael.WeatherAPI.model.response.CurrentWeatherResponse;
import com.raphael.WeatherAPI.model.response.WeatherForecastResponse;
import com.raphael.WeatherAPI.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WeatherService {

    private final String API_KEY = "e30fcfa99c63f0e68d5d5a4e7bdd089a";
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WeatherRepository weatherRepository;

    public WeatherService(RestTemplate restTemplate, WeatherRepository weatherRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.weatherRepository = weatherRepository;
        this.objectMapper = objectMapper;
    }


    /**
     * Retrieves the current weather for the given city.
     *
     * @param input the name of the city to retrieve the weather for
     * @return the current weather details for the given location
     */
    public Optional<CurrentWeather> getCurrentWeather(String input) {
        String cityName = formatLocationString(input);
        Optional<Location> optionalLocation = getLatLonFromLocation(cityName);

        if (optionalLocation.isPresent()) {
            Location actualLocation = optionalLocation.get();
            String uri = "https://api.openweathermap.org/data/2.5/weather?lat=" + actualLocation.latitude() +
                    "&lon=" + actualLocation.longitude() + "&appid=" + API_KEY;

            try {
                String response = restTemplate.getForObject(uri, String.class);
                Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {});

                if (!responseMap.isEmpty()) {
                    CurrentWeatherResponse currentWeatherResponse = objectMapper.convertValue(responseMap, CurrentWeatherResponse.class);
                    CurrentWeather currentWeather = mapCurrentWeather(currentWeatherResponse, cityName);
                    return Optional.of(currentWeather);
                }

            } catch (Exception e) {
                logger.error("Error occurred while retrieving or deserializing the location data: {}", e.getMessage());
            }
        } else {
            logger.error("Location data is not available for the given city: {}", cityName);
        }
        return Optional.empty();
    }


    /**
     * Retrieves the weather forecast for the given city.
     *
     * @param input the name of the city to retrieve the weather forecast for
     * @return a list of weather forecasts for the next few days
     */
    public List<WeatherForecast> getWeatherForecast(String input) {
        String cityName = formatLocationString(input);
        String location = getLocationName(cityName);
        Optional<Location> optionalLocation = getLatLonFromLocation(cityName);

        if (optionalLocation.isPresent()) {
            Location actualLocation = optionalLocation.get();
            String uri = "https://api.openweathermap.org/data/2.5/forecast?lat=" + actualLocation.latitude() +
                    "&lon=" + actualLocation.longitude() + "&appid=" + API_KEY;

            try {
                String response = restTemplate.getForObject(uri, String.class);
                Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {});
                ArrayList<?> weatherResponseList = (ArrayList<?>) responseMap.get("list");
                ArrayList<WeatherForecast> weatherForecasts = new ArrayList<>();
                ArrayList<String> daysInForecast = new ArrayList<>();

                for (Object o : weatherResponseList) {
                    Map<String, Object> listObjectMap = objectMapper.convertValue(o, new TypeReference<>() {});

                    if (!listObjectMap.isEmpty()) {
                        WeatherForecastResponse weatherForecastResponse = objectMapper.convertValue(listObjectMap, WeatherForecastResponse.class);
                        WeatherForecast weatherForecast = mapWeatherForecast(weatherForecastResponse, location, daysInForecast);

                        if (weatherForecast != null) {
                            weatherForecasts.add(weatherForecast);

                            if (weatherForecasts.size() == 5) {
                                break;
                            }
                        }
                    }
                }
                return weatherForecasts;

            } catch (Exception e) {
                logger.error("Error occurred while retrieving or deserializing the location data: {}", e.getMessage());
            }
        } else {
            logger.error("Location data is not available for the given city: {}", cityName);
        }
        return new ArrayList<>();
    }


    /**
     * Helper method that generates the latitude and longitude based on the given city name.
     *
     * @param cityName the name of the city to retrieve the latitude and longitude for
     * @return an optional Location object containing the latitude and longitude for the given city
     */
    public Optional<Location> getLatLonFromLocation(String cityName) {
        String baseUri = "https://api.openweathermap.org/geo/1.0/direct";

        try {
            URI uri = new URI(baseUri + "?q=" + cityName + "&limit=1&appid=" + API_KEY);
            String jsonResponse = restTemplate.getForObject(uri, String.class);

            // testing to see if we can just convert this response straight into our object
            // Map the string Response staight to a JSON object instead, and then get what we want from inside that object, and save to Location Object.....
            Location myLocation = objectMapper.readValue(jsonResponse, Location.class);


            List<Map<String, Object>> responseList = objectMapper.readValue(
                    jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class,
                            Map.class)
            ); // to make things much easier, just create a class which takes in responseList as fields and set this response as a class rather than this.

            if (!responseList.isEmpty()) {
                Location location = objectMapper.convertValue(responseList.get(0), Location.class);
                return Optional.of(location);
            }

        } catch (Exception e) {
            logger.error("Error occurred while retrieving or deserializing the location data: {}", e.getMessage());
        }

        return Optional.empty();
    }


    /**
     * Helper method that maps the CurrentWeatherResponse to a CurrentWeather object.
     *
     * @param response  the CurrentWeatherResponse object
     * @param cityName  the name of the city
     * @return the mapped CurrentWeather object
     */
    private CurrentWeather mapCurrentWeather(CurrentWeatherResponse response, String cityName) {
        Double temperature = (Double) response.main().get("temp");
        temperature -= 273.15;
        int tempAsInt = (int) Math.round(temperature);
        int humidity = (Integer) response.main().get("humidity");
        Double windSpeed = (Double) response.wind().get("speed");
        String description = (String) response.weather().get(0).get("description");
        int id = (int) response.weather().get(0).get("id");

        return new CurrentWeather(cityName, id, tempAsInt, humidity, windSpeed, description);
    }


    /**
     * Helper method that maps the WeatherForecastResponse to a WeatherForecast object.
     *
     * @param response         the WeatherForecastResponse object
     * @param location         the location name
     * @param daysInForecast   a list of days already included in the forecast
     * @return the mapped WeatherForecast object, or null if the day is already included in the forecast
     */
    private WeatherForecast mapWeatherForecast(WeatherForecastResponse response, String location, List<String> daysInForecast) {
        Double temperature = Double.parseDouble(response.main().get("temp").toString());
        temperature -= 273.15;
        int tempAsInt = (int) Math.round(temperature);
        int humidity = (int) response.main().get("humidity");
        Double windSpeed = Double.parseDouble(response.wind().get("speed").toString());
        String description = (String) response.weather().get(0).get("description");
        int id = (int) response.weather().get(0).get("id");
        int dateAsUnix = (int) response.date();
        int dayAsInt = (int) ((double) (dateAsUnix / 86400) + 4) % 7;
        String dayOfTheWeek = getDayOfWeek(dayAsInt);

        if (!daysInForecast.contains(dayOfTheWeek)) {
            daysInForecast.add(dayOfTheWeek);
            return new WeatherForecast(location, dayOfTheWeek, id, tempAsInt, humidity, windSpeed, description);
        }

        return null;
    }


    /**
     * Formats the location string by replacing spaces with underscores and converting it to lowercase.
     *
     * @param input the input string to be formatted
     * @return the formatted location string
     */
    private String formatLocationString(String input) {
        return input.replace(" ", "_").toLowerCase();
    }


    /**
     * Retrieves the location name by converting the city name to title case and replacing underscores with spaces.
     *
     * @param cityName the name of the city
     * @return the formatted location name
     */
    private String getLocationName(String cityName) {
        return cityName.substring(0, 1).toUpperCase() + cityName.substring(1).replace("_", " ");
    }


    /**
     * Retrieves the day of the week based on the given integer value.
     *
     * @param dayAsInt the integer value representing the day of the week (0-6)
     * @return the day of the week as a three-letter abbreviation (e.g., "Mon", "Tue", etc.)
     */
    private String getDayOfWeek(int dayAsInt) {
        return switch (dayAsInt) {
            case 0 -> "Mon";
            case 1 -> "Tue";
            case 2 -> "Wed";
            case 3 -> "Thu";
            case 4 -> "Fri";
            case 5 -> "Sat";
            default -> "Sun";
        };
    }
}
