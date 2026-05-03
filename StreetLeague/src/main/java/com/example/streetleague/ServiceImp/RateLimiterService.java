package com.example.streetleague.ServiceImp;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<Long, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        Refill refill = Refill.greedy(4, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(4, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public Bucket getBucket(Long userId) {
        return buckets.computeIfAbsent(userId, id -> createBucket());
    }

    public boolean tryConsume(Long userId) {
        return getBucket(userId).tryConsume(1);
    }
}