package com.example.demo.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ObjectRedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T get(String key, Class<T> clazz) throws JsonMappingException, JsonProcessingException {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json == null)
            return null;
        return objectMapper.readValue(json, clazz);
    }

    public <T> void set(String key, T object, int ttlTime, TimeUnit ttlTimeUnit) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(object);
        redisTemplate.opsForValue().set(key, json, ttlTime, ttlTimeUnit);
    }

    // public <T> void set(String key, T object, long ttlSeconds) {
    // redisTemplate.opsForValue().set(key, object, ttlSeconds, TimeUnit.SECONDS);
    // }

    // public <T> T get(String key, Class<T> clazz) {
    // Object value = redisTemplate.opsForValue().get(key);
    // if (value == null)
    // return null;
    // return clazz.cast(value);
    // }
}