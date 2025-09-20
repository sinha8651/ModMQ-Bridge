package com.application.close.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5261600905049703426L;

	private final String message;

	public ResourceNotFoundException(String message) {
		super(message);
		this.message = message;
	}

	public ResourceNotFoundException(String resourceName, String fieldName, long fieldValue) {
		this(String.format("%s not found with %s: %d", resourceName, fieldName, fieldValue));
	}

	public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
		this(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
	}

}
