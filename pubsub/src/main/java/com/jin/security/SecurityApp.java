package com.jin.security;

import com.jin.hr.Employee;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SecurityApp {

    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {
        InitialContext context = new InitialContext();
        Topic topic = (Topic) context.lookup("topic/empTopic");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            jmsContext.setClientID("securityApp");
            JMSConsumer consumer = jmsContext.createDurableConsumer(topic, "subscription1");
            consumer.close();

            Thread.sleep(10000);

            consumer = jmsContext.createDurableConsumer(topic, "subscription1");
            Message message = consumer.receive();
            Employee employee = message.getBody(Employee.class);
            System.out.println(employee.getFirstName());

            consumer.close();
            jmsContext.unsubscribe("subscription1");
        }
    }
}
