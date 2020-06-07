package com.jin.messagestructure;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageDelayDemo {

    public static void main(String[] args) throws NamingException {

        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/myQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
            JMSContext jmsContext = cf.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
            producer.setDeliveryDelay(3000);
            producer.send(queue, "Arise Awake and stop not till the goal is reached");

            Message messageReceived = jmsContext.createConsumer(queue).receive(5000);
            System.out.println(messageReceived);
        }
    }
}
