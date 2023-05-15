package com.raphael.WeatherAPI.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Weather {

    private String location;
    private String temperature;
    private String humidity;
    private String windSpeed;
    private String description;
}
