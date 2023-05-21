package com.raphael.WeatherAPI.repository;

import com.raphael.WeatherAPI.model.CurrentWeather;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends MongoRepository<CurrentWeather, String> {}
