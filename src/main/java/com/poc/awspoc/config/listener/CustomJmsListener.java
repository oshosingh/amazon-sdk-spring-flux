package com.poc.awspoc.config.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomJmsListener implements MessageListener{
	
//	@JmsListener(destination = "test")
	public void receiveMessage(String message) {
		try {
			log.atInfo().addArgument(message).log("message received : {}");
		}
		catch(Exception e) {
			log.atError().setCause(e).log("exception caught ");
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message message) {
		
		log.atInfo().log("Inside onMessage method");
		
		try {
			log.atInfo().addArgument(message.getStringProperty("Message")).log("message received in onMessage : {}");
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}
