package com.handson.user.publish;

import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserChangePublisher {

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	Topic userChangeSubscriberTopic;

	private static final Logger log = LoggerFactory.getLogger(UserChangePublisher.class);

	public String publish(UserAccountEvent userAccountEvent) {
		try {
			jmsTemplate.convertAndSend(userChangeSubscriberTopic, userAccountEvent);
			log.info("Event dispatched successfully");
			log.debug(String.format("Event [%s] dispatched successfully to Topic", userAccountEvent));
			return "success";
		} catch (JmsException e) {
			log.info("Event dispatching failed for user change");
			log.error(String.format("Event [%s] dispatching to topic failed", userAccountEvent), e);
		}
		return "failed";
	}
}
