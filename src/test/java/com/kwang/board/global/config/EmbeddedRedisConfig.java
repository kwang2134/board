package com.kwang.board.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@TestConfiguration
public class EmbeddedRedisConfig {
    private RedisServer redisServer;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory("127.0.0.1", 6379);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    @PostConstruct
    public void redisServer() throws IOException {
        redisServer = new RedisServer(6379);
        try {
            redisServer.start();
        } catch (Exception e) {
            log.error("Error starting Redis server", e);
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}