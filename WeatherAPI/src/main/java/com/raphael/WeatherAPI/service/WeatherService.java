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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WeatherService {

    private final String API_KEY = "e30fcfa99c63f0e68d5d5a4e7bdd089a";

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
     * @param input is the name of the city we are searching the weather for
     * @return the current weather details for the given location
     */
    public Optional<CurrentWeather> getCurrentWeather(String input) {

        // Replaces spaces with underscores for OpenWeather API
        String cityName = input.replace(" ", "_").toLowerCase();

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
                    temperature -= 273.15; // convert from kelvin to Celsius
                    int tempAsInt = (int) Math.round(temperature);
                    int humidity = (Integer) currentWeatherResponse.main().get("humidity");
                    Double windSpeed = (Double) currentWeatherResponse.wind().get("speed");
                    String description = (String) currentWeatherResponse.weather().get(0).get("description");

                    // Capitalize first letter of given cityName and remove underscores
                    String location = cityName
                            .substring(0, 1).toUpperCase() +
                            cityName.substring(1).replace("_", " ");


                    // Create a new CurrentWeather object with the extracted values
                    CurrentWeather currentWeather = new CurrentWeather(location, tempAsInt, humidity, windSpeed, description);

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


    public List<WeatherForecast> getWeatherForecast(String input) {

        // Replaces spaces with underscores for OpenWeather API
        String cityName = input.replace(" ", "_").toLowerCase();

        // Capitalize first letter of given cityName and remove underscores <<<< move these two methods to a formatLocationString method or something
        String location = cityName
                .substring(0, 1).toUpperCase() +
                cityName.substring(1).replace("_", " ");

        Optional<Location> optionalLocation = getLatLonFromLocation(cityName);
        String baseUri = "https://api.openweathermap.org/data/2.5/forecast";

        if (optionalLocation.isPresent()) {

            Location actualLocation = optionalLocation.get();

            try {
                URI uri = new URI(baseUri + "?lat=" + actualLocation.latitude() + "&lon=" + actualLocation.longitude() + "&appid=" + API_KEY);

                // Get the JSON response from the OpenWeather API. Get it as a string since objectMapper can handle the rest
                String response = restTemplate.getForObject(uri, String.class);

                // Deserialize the JSON array into a list of location maps, since we are receiving an array from OpenWeather API
                Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>(){});

                // Get list from within the response map. All values needed are inside this arraylist
                ArrayList<?> weatherResponseList = (ArrayList<?>) responseMap.get("list");

                // ArrayList to store the weather forecast
                ArrayList<WeatherForecast> weatherForecasts = new ArrayList<>();

                // ArrayList to store current days in forecast
                ArrayList<String> daysInForecast = new ArrayList<>();

                // Loop and get each of the forecasted weathers, and add to the WeatherForecast List
                for (Object o : weatherResponseList) {
                    // Convert the map inside the weatherResponseList to a Map that we can pull the data from and place into our WeatherForecast Object
                    Map<String, Object> listObjectMap = objectMapper.convertValue(o, new TypeReference<>() {});

                    if (!listObjectMap.isEmpty()) {
                        // Use ObjectMapper to map the required fields from the JSON into our CurrentWeatherResponse object. Adding this object as an additional Layer due to the nested nature of the JSON response
                        WeatherForecastResponse weatherForecastResponse = objectMapper.convertValue(listObjectMap, WeatherForecastResponse.class);

                        // Extract the fields we need for the CurrentWeather object from CurrentWeatherResponse
                        Double temperature = Double.parseDouble(weatherForecastResponse.main().get("temp").toString());
                        temperature -= 273.15; // convert from Kelvin to Celsius
                        int tempAsInt = (int) Math.round(temperature);
                        int humidity = (int) weatherForecastResponse.main().get("humidity");
                        Double windSpeed = Double.parseDouble(weatherForecastResponse.wind().get("speed").toString());
                        String description = (String) weatherForecastResponse.weather().get(0).get("description");
                        int id = (int) weatherForecastResponse.weather().get(0).get("id");


                        // Convert Unix date to a day of the week
                        // day of week = (floor(T / 86400) + 4) mod 7.
                        int dateAsUnix = (int) weatherForecastResponse.date();
                        int dayAsInt = (int) ((double) (dateAsUnix / 86400) + 4) % 7;

                        // enhanced switch statement to map day of week based on dayAsInt
                        String dayOfTheWeek = switch (dayAsInt) {
                            case 0 -> "Mon";
                            case 1 -> "Tue";
                            case 2 -> "Wed";
                            case 3 -> "Thu";
                            case 4 -> "Fri";
                            case 5 -> "Sat";
                            default -> "Sun";
                        };

                        // Check if day has already been forecasted, if it has, exit current loop iteration, since we only want 1 of each day
                        // Would take multiple readings, but OpenWeatherApi aren't clear what each reading is (AM/PM etc.)
                        // Could convert the unix time, but for the scale of this project, not required.
                        if (!daysInForecast.contains(dayOfTheWeek)) {
                            daysInForecast.add(dayOfTheWeek);

                            // Create a new CurrentWeather object with the extracted values
                            WeatherForecast weatherForecast = new WeatherForecast(location, dayOfTheWeek, id, tempAsInt, humidity, windSpeed, description);
                            weatherForecasts.add(weatherForecast);

                            if (weatherForecasts.size() == 5) break;
                        }
                    }
                } // end of for loop

                return weatherForecasts;

            } catch (URISyntaxException e) {
                logger.error("Error occurred while building URL from cityName: {}", e.getMessage());
            } catch (Exception e) { // objectMapper.readValue possibly throwing two JSON exceptions. Have URISyntaxException for specific logging.
                logger.error("Error occurred while retrieving or deserializing the location data: {}", e.getMessage());
            }
        } else {
            logger.error("Location data is not available for the given city: {}", cityName);
        }
        return new ArrayList<>();
    }


    /**
     * Helper method that generates the lat/lon based on the given city name. This is required for OpenWeatherMap API, as it does not take a cityName and return relevant weather details
     * @param cityName is the name of the city we are searching the weather for
     * @return an optional Location object containing the lat/lon for the given cityName
     */
    private Optional<Location> getLatLonFromLocation(String cityName) {
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




































