package com.raphael.WeatherAPI.repository;

import com.raphael.WeatherAPI.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends MongoRepository<Location, String> {}
