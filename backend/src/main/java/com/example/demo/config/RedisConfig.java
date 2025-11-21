package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer; // 💡 使用 Generic
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

        // 注入 Redis Host/Port (保留以強制使用 Docker 服務名)
        @Value("${spring.redis.host}")
        private String redisHost;

        @Value("${spring.redis.port}")
        private int redisPort;

        // 1. 手動定義 LettuceConnectionFactory (保留以解決 Docker 連接問題)
        @Bean
        public LettuceConnectionFactory redisConnectionFactory() {
                RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
                System.out.println("DEBUG: Connecting to Redis at " + redisHost + ":" + redisPort);
                return new LettuceConnectionFactory(config);
        }

        // 2. 配置 CacheManager (用於 @Cacheable)
        @Bean
        public CacheManager cacheManager(RedisConnectionFactory factory) {

                // 💡 替換成 GenericJackson2JsonRedisSerializer
                // 它會自動處理 JSON 中的類型資訊，且不會影響 Web 的 ObjectMapper。
                RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();

                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                // 可設定預設 TTL，例如 30 分鐘
                                .entryTtl(Duration.ofMinutes(30))

                                // 設置 Key 為 String 序列化
                                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))

                                // 設置 Value 為 Generic Jackson JSON 序列化
                                .serializeValuesWith(SerializationPair.fromSerializer(jsonSerializer))

                                // 禁用將 null 值寫入快取
                                .disableCachingNullValues();

                return RedisCacheManager.builder(factory)
                                .cacheDefaults(config)
                                .build();
        }

        // 3. 配置 RedisTemplate (用於手動操作 RedisService)
        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(factory);

                // Key 序列化器：確保 Key 是可讀的 String
                RedisSerializer<String> stringSerializer = new StringRedisSerializer();
                template.setKeySerializer(stringSerializer);
                template.setHashKeySerializer(stringSerializer);

                // 💡 替換成 GenericJackson2JsonRedisSerializer
                RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
                template.setValueSerializer(jsonSerializer);
                template.setHashValueSerializer(jsonSerializer);

                template.afterPropertiesSet();
                return template;
        }
}