package com.jin.grouping;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class MessageGroupingDemo {

    public static void main(String[] args) throws Exception {
        InitialContext context = new InitialContext();
        Queue queue = (Queue) context.lookup("queue/myQueue");
        Map<String, String> receivedMessages = new ConcurrentHashMap<>();

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext1 = cf.createContext();
             JMSContext jmsContext2 = cf.createContext()) {
            JMSProducer producer = jmsContext1.createProducer();
            JMSConsumer consumer1 = jmsContext2.createConsumer(queue);
            consumer1.setMessageListener(new MyListener("Consumer-1", receivedMessages));
            JMSConsumer consumer2 = jmsContext2.createConsumer(queue);
            consumer2.setMessageListener(new MyListener("Consumer-2", receivedMessages));

            int count = 10;
            TextMessage[] messages = new TextMessage[count];
            for (int i = 0; i < count; i++) {
                messages[i] = jmsContext1.createTextMessage("Group-0 message " + i);
                messages[i].setStringProperty("JMSXGroupId", "Group-0");
                producer.send(queue, messages[i]);
            }

            Thread.sleep(2000);

            for (TextMessage message : messages) {
                if (!receivedMessages.get(message.getText()).equals("Consumer-1")) {
                    throw new IllegalAccessException("Group Message " + message.getText() + " has gone to the wrong receiver");
                }
            }
        }
    }
}

class MyListener implements MessageListener {

    private final String name;
    private final Map<String, String> receivedMessages;

    MyListener(String name, Map<String, String> receivedMessages) {
        this.name = name;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println("Message Received is " + textMessage.getText());
            System.out.println("Listener Name " + name);
            receivedMessages.put(textMessage.getText(), name);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

