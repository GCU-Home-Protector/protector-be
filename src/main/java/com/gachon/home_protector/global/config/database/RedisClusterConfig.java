package com.gachon.home_protector.global.config.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

@Profile("dev")
@Configuration
public class RedisClusterConfig {

    @Value("${spring.data.redis.cluster.nodes}")
    private String host;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(6379);

        // SSL 사용 위해
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder().useSsl().disablePeerVerification().build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }


    @Bean
    public ObjectMapper objectMapperWithAuditing() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapperWithAuditing) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapperWithAuditing);

        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        return redisTemplate;
    }
}
