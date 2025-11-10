package com.application.close.exception;

import lombok.Getter;

@Getter
public class ModbusOperationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -825682828962349425L;

	private final String message;

	public ModbusOperationException(String message) {
		super();
		this.message = message;
	}

}
