package com.jin.messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessagePropertiesDemo {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/myQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
            TextMessage message = jmsContext.createTextMessage("Arise Awake and stop not till the goal is reached");
            message.setBooleanProperty("loggedIn", true);
            message.setStringProperty("userToken", "abc123");
            producer.send(queue, message);

            Message messageReceived = jmsContext.createConsumer(queue).receive(5000);
            System.out.println(messageReceived);
            System.out.println(messageReceived.getBooleanProperty("loggedIn"));
            System.out.println(messageReceived.getStringProperty("userToken"));
        }
    }
}
