package com.raphael.WeatherAPI.service;

import com.raphael.WeatherAPI.model.CurrentWeather;
import com.raphael.WeatherAPI.model.WeatherDescription;
import com.raphael.WeatherAPI.model.WeatherGeocoding;
import com.raphael.WeatherAPI.model.WeatherReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class WeatherService {

    private final String API_KEY = "cfe749fee8e119f7b17df5d7b4ed3301";

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * Gets the cityName, calls an internal method to get the lat/lon, and produces the current weather for the given location
     * @param cityName is the name of the city we are searching the weather for
     * @return the current weather details for the given location
     * @throws URISyntaxException potentially throws this exception if it cannot build URI correctly
     */
    public CurrentWeather getCurrentWeather(String cityName) throws URISyntaxException {

        WeatherGeocoding weatherGeocodingLatLon = getLatLonFromCityName(cityName);
        String baseUri = "https://api.openweathermap.org/data/2.5/weather";

        try {
            URI uri = new URI(baseUri + "?lat=" + weatherGeocodingLatLon.lat() + "&lon=" + weatherGeocodingLatLon.lon() + "&appid=" + API_KEY);

            WeatherReport weatherReport = restTemplate.getForObject(uri.toString(), WeatherReport.class);

            // do processing on the weatherReport, such as converting to CurrentWeather object by mapping fields to currentWeather fields.

        } catch (URISyntaxException e) {
            logger.error("Error occurred while getting weather: {}", e.getMessage());
        }

        return new WeatherDescription("", "", "", "", "");

    }


    /**
     * Helper method that generates the lat/lon based on the given city name. This is required for openweathermap API, as it does not take a cityName and return relevant weather details
     * @param cityName is the name of the city we are searching the weather for
     * @return the lat/lon for the given cityName
     * @throws URISyntaxException potentially throws this exception if it cannot build URI correctly
     */
    private WeatherGeocoding getLatLonFromCityName(String cityName) throws URISyntaxException {

        String baseUri = "https://api.openweathermap.org/geo/1.0/direct";

        try {
            URI uri = new URI(baseUri + "?q=" + cityName + "&limit=1&appid=" + API_KEY);

            WeatherGeocoding weatherGeocoding = restTemplate.getForObject(uri.toString(), WeatherGeocoding.class);

            return weatherGeocoding;

        } catch (URISyntaxException e) {
            logger.error("Error occurred while building url from cityName: {}", e.getMessage());
        }

        return new WeatherGeocoding(0.0, 0.0);
    }
}


// take long/lat from data and get weather data

// map to Weather object

// return in this method.


//    String url = "https://api.krakenflex.systems/interview-tests-mock-api/v1/outages";
//
//        headers = new HttpHeaders();
//                headers.set("x-api-key", "EltgJ5G8m44IzwE6UN2Y4B4NjPW77Zk6FJK3lL23");
//
//                entity = new HttpEntity<>(headers);
//
//        try {
//        ResponseEntity<List<Outage>> responseEntity = restTemplate.exchange(
//        url,
//        HttpMethod.GET,
//        entity,
//        new ParameterizedTypeReference<List<Outage>>() {}
//        );
//        List<Outage> outages = responseEntity.getBody();
//        logger.info("Outages list: " + outages);
//        return outages;
//        } catch (RestClientException e) {
//        logger.error("Error occurred while getting all outages: {}", e.getMessage());
//        return new ArrayList<>();
//        }