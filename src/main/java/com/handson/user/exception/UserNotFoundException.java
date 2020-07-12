package com.handson.user.exception;

public class UserNotFoundException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7106413128413819832L;

	public UserNotFoundException(Integer id) {
		super("Could not find user with id " + id, new Exception("user id not present"));
	}

	public UserNotFoundException() {
		super("Could not find users with given parameters", new Exception("no user matching the criteria provided"));
	}

	public UserNotFoundException(String msg, Exception exception) {
		super(msg,exception);
	}

}
