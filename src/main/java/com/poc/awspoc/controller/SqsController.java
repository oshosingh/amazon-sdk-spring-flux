package com.poc.awspoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.poc.awspoc.config.SqsConfig;
import com.poc.awspoc.config.listener.DynamicJmsListener;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sqs")
@Slf4j
public class SqsController {
	
	@Autowired
	private SqsConfig sqsConfig;
	
	@Autowired
	private DynamicJmsListener jmsListenerRegistrar;
	
	private String queueUrl = "https://sqs.ap-south-1.amazonaws.com/827169409518/test";
	
	@PostMapping("/create/{name}")
	public String createSqs(@PathVariable("name") String queueName) {
		
		AmazonSQS sqsClient = sqsConfig.getSqsClient();
		
		CreateQueueRequest queueRequest = new CreateQueueRequest(queueName);
		String standardQueueUrl = sqsClient.createQueue(queueRequest).getQueueUrl();
		
		return standardQueueUrl;
		
	}
	
	@GetMapping("/consume")
	public String consumeSQS() {
		ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl)
				.withWaitTimeSeconds(1)
				.withMaxNumberOfMessages(1);
		AmazonSQS sqsClient = sqsConfig.getSqsClient();
		
		List<Message> messages = sqsClient.receiveMessage(request).getMessages();
		
		if(messages.size() == 0) return "no message";
		
		log.atInfo().addArgument(messages.size()).addArgument(messages.get(0).getBody()).log("received : {} : {}");
		
		log.atInfo().log("Deleteing messages");
		sqsClient.deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle(messages.get(0).getReceiptHandle()));
		
		
		return "consuming";
	}
	
	@PostMapping("/register/{name}")
	String registerListener(@PathVariable("name") String queueName) {
		jmsListenerRegistrar.createListener(queueName);
		return "added";
	}

}
