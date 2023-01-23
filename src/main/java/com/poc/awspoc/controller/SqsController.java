package com.poc.awspoc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.poc.awspoc.config.SnsConfig;
import com.poc.awspoc.config.SqsConfig;
import com.poc.awspoc.config.listener.DynamicJmsListenerRegistrar;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sqs")
@Slf4j
public class SqsController {
	
	@Autowired
	private SqsConfig sqsConfig;
	
	@Autowired
	private SnsConfig snsConfig;
	
	@Autowired
	private DynamicJmsListenerRegistrar jmsListenerRegistrar;
	
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
	
	@PostMapping("/create/services/{name}")
	Map<String, String> create(@PathVariable("name") String serviceName) {
		
		Map<String, String> responseMap = new HashMap<>();
		
		AmazonSNS snsClient = snsConfig.getSnsClient();
		CreateTopicRequest snsRequest = new CreateTopicRequest(serviceName);
		String snsTopicArn = snsClient.createTopic(snsRequest).getTopicArn();
		
		responseMap.put("Sns topic arn", snsTopicArn);
		log.atInfo().log("Sns topic created");
		
		AmazonSQS sqsClient = sqsConfig.getSqsClient();
		CreateQueueRequest sqsRequest = new CreateQueueRequest(serviceName);
		CreateQueueResult createQueueResult = sqsClient.createQueue(sqsRequest);
		
		responseMap.put("Sqs queue url", createQueueResult.getQueueUrl());
		log.atInfo().log("Create sqs queue");
		
		String subscriptionArn = Topics.subscribeQueue(snsClient, sqsClient, snsTopicArn, createQueueResult.getQueueUrl());
		
		responseMap.put("Subscription Arn", subscriptionArn);
		
		log.atInfo().log("Subscribed sns to sqs");
		
		sqsClient.sendMessage(createQueueResult.getQueueUrl(), "api message");
		
		return responseMap;
	}

}
