package com.jin.claimmanagement;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ClaimManagement {

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext initialContext = new InitialContext();
        Queue claimQueue = (Queue) initialContext.lookup("queue/claimQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
//            JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "hospitalId=2");
//            JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "claimAmount between 1000 and 5000");
            JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "doctorName like 'Joh_'");

            ObjectMessage objectMessage = jmsContext.createObjectMessage();
//            objectMessage.setIntProperty("hospitalId", 1);
//            objectMessage.setDoubleProperty("claimAmount", 1000);
            objectMessage.setStringProperty("doctorName", "John");
            Claim claim = new Claim();
            claim.setHospitalId(1);
            claim.setClaimAmount(1000);
            claim.setDoctorName("John");
            claim.setDoctorType("gyna");
            claim.setInsuranceProvider("blue cross");
            objectMessage.setObject(claim);

            producer.send(claimQueue, objectMessage);
            Claim receiveBody = consumer.receiveBody(Claim.class);
            System.out.println(receiveBody.getClaimAmount());

        }
    }
}
