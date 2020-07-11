package com.handson.user.publish;

import java.io.Serializable;

public class UserAccountEvent implements Serializable {
	private static final long serialVersionUID = -2803449835207926087L;
	private UserAccountEventType changeEvent;
	private String message;

	public UserAccountEvent(UserAccountEventType changeEvent, String message) {
		super();
		this.changeEvent = changeEvent;
		this.message = message;
	}

	public UserAccountEventType getChangeEvent() {
		return changeEvent;
	}

	public void setChangeEvent(UserAccountEventType changeEvent) {
		this.changeEvent = changeEvent;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "UserAccountEvent [changeEvent=" + changeEvent + ", message=" + message + "]";
	}
}
