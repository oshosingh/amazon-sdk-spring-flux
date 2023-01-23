package com.poc.awspoc.config.listener;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import com.poc.awspoc.config.JmsConfig;
import com.poc.awspoc.config.RateLimiter;
import com.poc.awspoc.exceptions.RateLimitThrottleException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableJms
public class DynamicJmsListenerRegistrar implements JmsListenerConfigurer {
	
	private JmsListenerEndpointRegistrar registrar;
	
	@Autowired
	private JmsConfig jmsConfig;
	
	@Autowired
	private RateLimiter rateLimiter;
	
	public static AtomicInteger integer = new AtomicInteger(0);
	
	public void createListener(String queueName) {
		
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId(queueName);
		endpoint.setDestination(queueName);
		
		endpoint.setMessageListener(message -> {
			try {
				String messageText = ((TextMessage) message).getText();
				
				// throttle condition
				if(!rateLimiter.hasTokens(queueName)) {
					log.atInfo().addArgument(queueName).log("Throttling message received from queue : {}");
					throw new RateLimitThrottleException("Too many requests");
				}
				
				log.atInfo().addArgument(messageText).addArgument(queueName).log("Consumed message : {} from queue : {}");
				
			} catch (JMSException e) {
				log.atError().addArgument(e.getMessage()).addArgument(queueName).log("Exception caught {} while listening to quueue {}");
				e.printStackTrace();
			}
			this.registrar.setContainerFactory(jmsConfig.jmsListenerContainerFactory());
		});
		
		registrar.registerEndpoint(endpoint);
	}
	
	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
		this.registrar = registrar;
		log.atInfo().log("Saved registrar local");
	}
}
