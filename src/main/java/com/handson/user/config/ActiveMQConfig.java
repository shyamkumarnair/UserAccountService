package com.handson.user.config;

import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class ActiveMQConfig {
	
	@Value("${spring.activemq.broker-url}")
	private String brokerUrl;
	
	@Bean
	public Queue queue()
	{
		return new ActiveMQQueue("useraccount.changes.queue.publish");
	}
	
	@Bean
	public Topic topic()
	{
		return new ActiveMQTopic("useraccount.changes.topic.publish");
	}
	
	@Bean
	public ActiveMQConnectionFactory getActiveMQConnectionFactory()
	{
		ActiveMQConnectionFactory amqFactory = new ActiveMQConnectionFactory();
		amqFactory.setBrokerURL(brokerUrl);
		return amqFactory;
	}
	
	@Bean
	public JmsTemplate getJmsTemplate()
	{
		return new JmsTemplate(getActiveMQConnectionFactory());
	}

}
