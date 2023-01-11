package com.poc.awspoc.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
public class SnsConfig {

    @Value("${aws.access.key}")
    private String awsAccessKey;

    @Value("${aws.secret.key}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.sns.endpoint}")
    private String snsEndpoint;

    @Value("${aws.sns.topic}")
    private String snsTopicName;

    private AWSCredentials getBasicCredentials() {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

    @Bean
    public AmazonSNS getSNSClient() {
        return AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getBasicCredentials()))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(snsEndpoint, region))
                .build();
    }

    @Bean
    public NotificationMessagingTemplate getSNSMessagingTemplate() {
        NotificationMessagingTemplate notificationMessagingTemplate = new NotificationMessagingTemplate(getSNSClient());
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.setSerializedPayloadClass(String.class);
        mappingJackson2MessageConverter.getObjectMapper().registerModule(new JavaTimeModule());
        notificationMessagingTemplate.setMessageConverter(mappingJackson2MessageConverter);
        notificationMessagingTemplate.setDefaultDestinationName(snsTopicName);
        return notificationMessagingTemplate;
    }

}
