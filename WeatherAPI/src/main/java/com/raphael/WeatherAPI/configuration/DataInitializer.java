package com.raphael.WeatherAPI.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.WeatherAPI.model.Location;
import com.raphael.WeatherAPI.repository.WeatherRepository;
import com.raphael.WeatherAPI.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

@Component
public class DataInitializer {

    private final WeatherRepository weatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @PostConstruct
    public void initializeData() {
        try {
            // Load the JSON file
            InputStream inputStream = getClass().getResourceAsStream("src/main/resources/data/locations.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Parse the JSON and populate the repository
            ObjectMapper objectMapper = new ObjectMapper();
            Location[] locationData = objectMapper.readValue(reader, Location[].class);
            weatherRepository.saveAll(Arrays.asList(locationData));
        } catch (IOException e) {
            logger.error("Error occurred while retrieving the location data file in DataInitializer class: {}", e.getMessage());
        }
    }
}
