package com.jin.welness;

import com.jin.hr.Employee;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class WelnessApp {

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext context = new InitialContext();
        Topic topic = (Topic) context.lookup("topic/empTopic");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            JMSConsumer consumer1 = jmsContext.createSharedConsumer(topic, "sharedConsumer");
            JMSConsumer consumer2 = jmsContext.createSharedConsumer(topic, "sharedConsumer");

            for (int i = 0; i < 10; i+=2) {
                Message message1 = consumer1.receive();
                Employee employee1 = message1.getBody(Employee.class);
                System.out.println("Consumer 1: " + employee1.getFirstName());

                Message message2 = consumer2.receive();
                Employee employee2 = message2.getBody(Employee.class);
                System.out.println("Consumer 2: " + employee2.getFirstName());
            }

        }
    }
}
