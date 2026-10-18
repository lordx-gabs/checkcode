package com.edu.checkcode.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

	public EmailAlreadyRegisteredException(String email) {
		super("A user is already registered with the email " + email);
	}
}
