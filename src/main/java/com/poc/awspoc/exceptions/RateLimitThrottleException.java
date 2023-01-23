package com.poc.awspoc.exceptions;

import lombok.Getter;

@Getter
public class RateLimitThrottleException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private String errorMessage;
	
	public RateLimitThrottleException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
	

}
