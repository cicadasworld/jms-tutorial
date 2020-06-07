package com.jin.clinicals;

import com.jin.model.Patient;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ClinicalsApp {

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            JMSProducer producer = jmsContext.createProducer();

            ObjectMessage objectMessage = jmsContext.createObjectMessage();
            Patient patient = new Patient();
            patient.setId(123);
            patient.setName("Bob");
            patient.setInsuranceProvider("Blue Cross Blue Shield");
            patient.setCopay(30d);
            patient.setAmountToBePayed(500d);
            objectMessage.setObject(patient);

            for (int i = 0; i < 10; i++) {
                producer.send(requestQueue, objectMessage);
            }
//            JMSConsumer consumer = jmsContext.createConsumer(replyQueue);
//            MapMessage replyMessage = (MapMessage) consumer.receive(30000);
//            System.out.println("Patient eligibility is: " + replyMessage.getBoolean("eligible"));
        }
    }
}
