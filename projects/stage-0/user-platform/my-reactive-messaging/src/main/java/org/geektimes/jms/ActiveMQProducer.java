package org.geektimes.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.annotation.Resource;
import javax.jms.*;
import java.time.Instant;

public class ActiveMQProducer {

    @Resource(name = "jms/ActiveMQConnectionFactory")
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @Resource(name = "jms/queue/MyQueue")
    private ActiveMQQueue activeMQQueue;

    @Resource(name = "jms/topic/MyTopic")
    private ActiveMQTopic activeMQTopic;

    private MessageProducer producer = null;
    private Session session = null;
    private Connection connection = null;

    private <T extends ActiveMQDestination> void init(T t) {
        try {
            // Create a Connection
            connection = activeMQConnectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = t;

            // Create a MessageProducer from the Session to the Topic or Queue
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void close () {
        try {
            session.close();
            connection.close();
            producer.close();
        } catch (JMSException e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public <T extends ActiveMQDestination> void send(T t, int count) {
        try {
            if (producer == null && session == null && connection == null) {
                init(t);
            }

            for (int i = 0; i < count; i++) {
                // Create a messages
                String text = "Hello world! at " + System.currentTimeMillis();
                TextMessage message = session.createTextMessage(text);

                // Tell the producer to send the message
                if (t instanceof ActiveMQQueue) {
                    System.out.println("Sent message: " + message.getText() + " to queue");
                } else {
                    System.out.println("Sent message: " + message.getText() + " to topic");
                }
                producer.send(message);
            }
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void sendToQueue(int count) {
        send(activeMQQueue, count);
    }

    public void sendToTopic(int count) {
        send(activeMQTopic, count);
    }
}
