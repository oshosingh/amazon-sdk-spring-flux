package com.poc.awspoc.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RateLimiter {

    private Long numTokens = 2L;

    private Map<String, Bucket> tokenBucketMap = new HashMap<>();
    
    public RateLimiter() {
    	// db call to update tokenBucketMap if app restarts
    }

    public Boolean hasTokens(String queueName) {
        if(tokenBucketMap.get(queueName).tryConsume(1)) {
        	log.atInfo().addArgument(totalTokens(queueName)).log("Left after consumption : {}");
            return true;
        }
        return false;
    }

    public Boolean isCustomerPresent(String queueName) {
        if(tokenBucketMap.containsKey(queueName)) return true;
        return false;
    }

    public void createBucketForCustomer(String queueName) {
        Refill refillRate = Refill.intervally(numTokens, Duration.ofSeconds(numTokens));
        Bucket bucket = Bucket.builder().addLimit(Bandwidth.classic(numTokens.intValue(), refillRate)).build();
        tokenBucketMap.put(queueName, bucket);
    }
    
    public Long totalTokens(String queueName) {
    	return tokenBucketMap.get(queueName).getAvailableTokens();
    }
}
