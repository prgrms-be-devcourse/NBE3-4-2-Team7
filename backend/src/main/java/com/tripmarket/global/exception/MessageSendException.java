package com.tripmarket.global.exception;

public class MessageSendException extends RuntimeException {
	public MessageSendException(String message, Throwable cause) {
		super(message, cause);
	}
}