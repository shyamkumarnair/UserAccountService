package com.handson.user.exception;

public class InvalidPasswordException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6744301654216630994L;

	public InvalidPasswordException() {
		super("Invalid password ", new Exception("Invalid password"));
	}
}
