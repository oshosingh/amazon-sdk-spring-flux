package com.poc.awspoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
public class SqsConfig {
	

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
    public AmazonSQS getSqsClient() {
    	AmazonSQS sqsClient = AmazonSQSClientBuilder.standard()
    			.withCredentials(new AWSStaticCredentialsProvider(getAwsCredentials()))
    			.withRegion(Regions.AP_SOUTH_1)
    			.build();
    	return sqsClient;
    }
    
    @Bean
    public SQSConnectionFactory getConnectionFactory() {
    	return SQSConnectionFactory.builder()
    			.withAWSCredentialsProvider(new AWSStaticCredentialsProvider(getAwsCredentials()))
    			.withRegion(Region.getRegion(Regions.AP_SOUTH_1))
    			.build();
    }
    

}
