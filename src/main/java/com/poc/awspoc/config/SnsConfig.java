package com.poc.awspoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

@Configuration
public class SnsConfig {
	

    @Value("${aws.access.key}")
    private String awsAccessKey;

    @Value("${aws.secret.key}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String region;
    
    private AWSCredentials getAwsCredentials() {
    	return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }
    
    @Bean
    public AmazonSNS getSnsClient() {
    	return AmazonSNSClientBuilder.standard()
    			.withCredentials(new AWSStaticCredentialsProvider(getAwsCredentials()))
    			.withRegion(Regions.AP_SOUTH_1)
    			.build();
    }

}
