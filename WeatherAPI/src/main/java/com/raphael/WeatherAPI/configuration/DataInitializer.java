package com.raphael.WeatherAPI.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raphael.WeatherAPI.model.Location;
import com.raphael.WeatherAPI.repository.WeatherRepository;
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

    public DataInitializer(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @PostConstruct
    public void initializeData() {
        try {
            // Load the JSON file
            InputStream inputStream = getClass().getResourceAsStream("/data.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Parse the JSON and populate the repository
            ObjectMapper objectMapper = new ObjectMapper();
            Location[] locationData = objectMapper.readValue(reader, Location[].class);
            weatherRepository.saveAll(Arrays.asList(locationData));
        } catch (IOException e) {
            // Handle the IOException
            e.printStackTrace(); // or log the error
        }
    }
}
