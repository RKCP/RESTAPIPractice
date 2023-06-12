package com.raphael.WeatherAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document("locations")
public record Location(
        @JsonProperty("name") String name,
        @JsonProperty("coord") Coordinates coordinates) {

    public record Coordinates(
            @JsonProperty("lat") Double latitude,
            @JsonProperty("lon") Double longitude) {}
}
