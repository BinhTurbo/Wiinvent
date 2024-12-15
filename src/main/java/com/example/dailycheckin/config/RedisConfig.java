package com.example.dailycheckin.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Cấu hình RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /**
     * Cấu hình RedissonClient để sử dụng Redis lock
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // Cấu hình kết nối Redis
        config.useSingleServer()
                .setAddress("redis://localhost:6379") // Địa chỉ Redis
                .setPassword(null) // Thêm mật khẩu nếu cần
                .setConnectionPoolSize(10) // Cấu hình pool kết nối
                .setConnectionMinimumIdleSize(2); // Số kết nối tối thiểu
        return Redisson.create(config);
    }
}
