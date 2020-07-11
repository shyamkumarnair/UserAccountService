package com.handson.user.exception;

public class UserNotFoundException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7106413128413819832L;

	public UserNotFoundException(Integer id) {
		super("Could not find user with id " + id);
	}

	public UserNotFoundException() {
		super("Could not find users with given parameters");
	}

	public UserNotFoundException(String msg, Exception exception) {
		super(msg,exception);
	}

}
