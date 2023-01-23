package com.poc.awspoc.config;

import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJms
@Slf4j
public class JmsConfig {
	
	@Autowired
	private SqsConfig sqsConfig;
	
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(sqsConfig.getConnectionFactory());
		factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		factory.setDestinationResolver(new DynamicDestinationResolver());
		factory.setErrorHandler(error -> {
			//log.atError().addArgument(error.getMessage()).log("exception in jms : {}");
		});
		
//		factory.setSessionTransacted(true);
		return factory;
	}

}
