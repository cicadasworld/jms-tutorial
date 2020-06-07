package com.jin.hr;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class HRApp {

    public static void main(String[] args) throws NamingException {
        InitialContext context = new InitialContext();
        Topic topic = (Topic) context.lookup("topic/empTopic");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            Employee employee = new Employee();
            employee.setId(123);
            employee.setFirstName("Jin");
            employee.setLastName("H");
            employee.setDesignation("Software Architect");
            employee.setEmail("jin@coder.com");
            employee.setPhone("123456");
            for (int i = 0; i < 10; i++) {
                jmsContext.createProducer().send(topic, employee);
            }
            System.out.println("Message Sent");
        }
    }
}
