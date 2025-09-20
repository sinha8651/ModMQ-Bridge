package com.application.close.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3617693990883087116L;

	private final String message;

	public BadRequestException(String message) {
		super();
		this.message = message;
	}

}
