package com.handson.user.publish;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import javax.jms.Topic;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.InvalidClientIDException;
import org.springframework.jms.core.JmsTemplate;

@SpringBootTest
class UserChangePublisherTest {

	@Mock
	JmsTemplate mockjmsTemplate;

	@Mock
	Topic mockUserChangeSubscriberTopic;

	@InjectMocks
	UserChangePublisher userChangePublisher = new UserChangePublisher();

	Fixture fixture = new Fixture();

	@Test
	void test_publish_newUser() {
		fixture.setArgumentCapture();
		fixture.createNewUserEvent();
		fixture.callService();
		fixture.thenConfirmJMSTemplateCalled();
		fixture.confirmSuccessResponse();
		fixture.confirmEventCapturedCorrect();
	}

	@Test
	void test_publish_modifyUser() {
		fixture.setArgumentCapture();
		fixture.createModifyUserEvent();
		fixture.callService();
		fixture.thenConfirmJMSTemplateCalled();
		fixture.confirmSuccessResponse();
		fixture.confirmEventCapturedCorrect();
	}

	@Test
	void test_publish_deleteUser() {
		fixture.setArgumentCapture();
		fixture.createDeleteUserEvent();
		fixture.callService();
		fixture.thenConfirmJMSTemplateCalled();
		fixture.confirmSuccessResponse();
		fixture.confirmEventCapturedCorrect();
	}

	@Test
	void test_publish_fail() {
		fixture.setArgumentCapture();
		fixture.createNewUserEvent();
		fixture.whenJMSTemplateCalledThrowError();
		fixture.callService();
		fixture.confirmFailureResponse();
	}

	class Fixture {
		ArgumentCaptor<UserAccountEvent> userAccountEventCaptor;
		UserAccountEvent userAccountEvent;
		String response;

		public void setArgumentCapture() {
			userAccountEventCaptor = ArgumentCaptor.forClass(UserAccountEvent.class);
		}

		public void whenJMSTemplateCalledThrowError() {
			doThrow(InvalidClientIDException.class).when(mockjmsTemplate).convertAndSend(Mockito.any(Topic.class),
					Mockito.any(UserAccountEvent.class));
		}

		public void confirmEventCapturedCorrect() {
			assertEquals(userAccountEvent, userAccountEventCaptor.getValue());
		}

		public void confirmSuccessResponse() {
			assertEquals(response, "success");
		}

		public void confirmFailureResponse() {
			assertEquals(response, "failed");
		}

		public void thenConfirmJMSTemplateCalled() {
			verify(mockjmsTemplate).convertAndSend(Mockito.any(Topic.class), userAccountEventCaptor.capture());
		}

		public void callService() {
			response = userChangePublisher.publish(userAccountEvent);
		}

		public void createNewUserEvent() {
			userAccountEvent = new UserAccountEvent(UserAccountEventType.NEW_USER_CREATED, "New user has been created");
		}

		public void createModifyUserEvent() {
			userAccountEvent = new UserAccountEvent(UserAccountEventType.USER_MODIFIED, "User details modified");
		}

		public void createDeleteUserEvent() {
			userAccountEvent = new UserAccountEvent(UserAccountEventType.USER_DELETED, "User details removed");
		}
	}
}
