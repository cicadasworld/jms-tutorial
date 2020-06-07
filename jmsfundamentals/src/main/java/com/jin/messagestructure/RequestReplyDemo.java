package com.jin.messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RequestReplyDemo {

    public static void main(String[] args) throws NamingException, JMSException {

        InitialContext context = new InitialContext();
        Queue requestQueue = (Queue) context.lookup("queue/requestQueue");
//        Queue replyQueue = (Queue) context.lookup("queue/replyQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
            TemporaryQueue replyQueue = jmsContext.createTemporaryQueue();
            TextMessage message = jmsContext.createTextMessage("Arise Awake and stop not till the goal is reached");
            message.setJMSReplyTo(replyQueue);
            producer.send(requestQueue, message);
            System.out.println(message.getJMSMessageID());

            Map<String, TextMessage> requestMessages = new HashMap<>();
            requestMessages.put(message.getJMSMessageID(), message);

            JMSConsumer consumer = jmsContext.createConsumer(requestQueue);
            TextMessage messageReceived = (TextMessage) consumer.receive();
            System.out.println(messageReceived.getText());

            JMSProducer replyProducer = jmsContext.createProducer();
            TextMessage replyMessage = jmsContext.createTextMessage("You are awesome!");
            replyMessage.setJMSCorrelationID(messageReceived.getJMSMessageID());
            replyProducer.send(messageReceived.getJMSReplyTo(), replyMessage);

            JMSConsumer replyConsumer = jmsContext.createConsumer(replyQueue);
//            System.out.println(replyConsumer.receiveBody(String.class));
            TextMessage replyReceived = (TextMessage) replyConsumer.receive();
            System.out.println(replyReceived.getJMSCorrelationID());
            System.out.println(requestMessages.get(replyReceived.getJMSCorrelationID()).getText());
        }
    }
}
