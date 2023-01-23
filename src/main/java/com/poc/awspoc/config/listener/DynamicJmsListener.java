package com.poc.awspoc.config.listener;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import com.poc.awspoc.config.JmsConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableJms
public class DynamicJmsListener implements JmsListenerConfigurer {
	
	/**
	 * @TODO
	 * check if keeping a copy of registrar at global scope can help make it dynamic
	 */
	
	private JmsListenerEndpointRegistrar registrar;
	
	@Autowired
	private JmsConfig jmsConfig;
	
	public void createListener(String queueName) {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId(queueName);
		endpoint.setDestination(queueName);
		endpoint.setMessageListener(message -> {
			try {
				String messageText = ((TextMessage) message).getText();
				log.atInfo().addArgument(messageText).addArgument(queueName).log("Received message : {} from queue : {}");
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
		
//		SimpleJmsListenerEndpoint jmsListenerEndpoint = new SimpleJmsListenerEndpoint();
//		jmsListenerEndpoint.setId("test");
//		jmsListenerEndpoint.setDestination("test");
//		jmsListenerEndpoint.setMessageListener(message -> {
//			try {
//				log.atInfo().addArgument(message.getJMSMessageID()).addArgument(message.getJMSDestination())
//					.log("Received Id : {} Destionation : {}");
//				log.atInfo().addArgument(((TextMessage) message).getText()).log("message object : {}");
//				registrar.setContainerFactory(jmsConfig.jmsListenerContainerFactory());
//			}
//			catch(JMSException e) {
//				log.atError().log("exception");
//				e.printStackTrace();
//			}
//		});
//		
//		log.atInfo().log("inside configure jms");
//		registrar.registerEndpoint(jmsListenerEndpoint);
	}
}
