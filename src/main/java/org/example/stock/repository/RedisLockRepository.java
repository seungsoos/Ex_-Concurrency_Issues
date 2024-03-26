package org.example.stock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Long key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key),"lock");
    }

    public Boolean unlock(Long key) {
        return redisTemplate.delete(generateKey(key));
    }


    private String generateKey(Long key) {
        return key.toString();
    }
}
