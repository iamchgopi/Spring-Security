package com.example.weather_service.service;

import com.example.weather_service.Repository.WeatherRepository;
import com.example.weather_service.entity.Weather;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @Cacheable(value = "weather", key = "#city")
    public String getWeatherByCity(String city) {
        System.out.println("Fetching data from DB for city: " + city);
        Optional<Weather> weather = weatherRepository.findByCity(city);
        return weather.map(Weather::getForecast).orElse("Weather data not available");
    }

    @CachePut(value = "weather", key = "#city")
    public String updateWeather(String city, String updatedWeather) {
        weatherRepository.findByCity(city).ifPresent(weather -> {
            weather.setForecast(updatedWeather);
            weatherRepository.save(weather);
        });
        return updatedWeather;
    }
//update the cache
    @Transactional
    @CacheEvict(value = "weather", key = "#city")
    public void deleteWeather(String city) {
        System.out.println("Removing weather data for city: " + city);
        weatherRepository.deleteByCity(city);
    }
}


//        @Cacheable:
//
//        Tells Spring to check the cache before executing the method.
//
//        If a cached value exists for the given key, the method is not executed; the cached value is returned.
//
//        If not, the method executes, and the result is stored in the cache using the specified key.
//
//        value = "weather":
//
//        Specifies the name of the cache (usually defined in your cache configuration, e.g., EhCache, Redis, Caffeine).
//
//        key = "#city":
//
//        Uses the method parameter city as the cache key (e.g., "New York").
//
//        This means each city will have a separate cached entry.


//     @CachePut:
//
//        Always executes the method.
//
//        Updates the cache with the return value, using #city as the key.
//
//        Useful when you want to synchronize the cache after an update.
//
//        Behavior:
//
//        If the city exists in the database, it updates the forecast and caches the new value.
//
//        If the city doesn’t exist, nothing happens—but it still caches updatedWeather, which may be misleading or incorrect.