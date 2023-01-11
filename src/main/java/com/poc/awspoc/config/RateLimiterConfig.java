package com.poc.awspoc.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.sql.Ref;
import java.time.Duration;
import java.util.Map;

@Configuration
public class RateLimiterConfig {

    private Long numTokens = 5L;

    private Map<String, Bucket> tokenBucketMap;

    public Boolean hasTokens(String customerId) {
        if(tokenBucketMap.get(customerId).tryConsume(numTokens)) {
            return true;
        }
        return false;
    }

    public Boolean isCustomerPresent(String customerId) {
        if(tokenBucketMap.containsKey(customerId)) return true;
        return false;
    }

    public void createBucketForCustomer(String customerId) {
        Refill refillRate = Refill.intervally(numTokens, Duration.ofMinutes(1));
        Bucket bucket = Bucket.builder().addLimit(Bandwidth.classic(numTokens.intValue(), refillRate)).build();
        tokenBucketMap.put(customerId, bucket);
    }
}
