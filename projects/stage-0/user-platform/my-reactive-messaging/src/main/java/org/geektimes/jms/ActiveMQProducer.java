package org.geektimes.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.annotation.Resource;
import javax.jms.*;

public class ActiveMQProducer {

    @Resource(name = "jms/ActiveMQConnectionFactory")
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @Resource(name = "jms/queue/MyQueue")
    private ActiveMQQueue activeMQQueue;

    @Resource(name = "jms/topic/MyTopic")
    private ActiveMQTopic activeMQTopic;

    public <T extends ActiveMQDestination> void run(T t) {
        try {
            // Create a Connection
            Connection connection = activeMQConnectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = t;

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            String text = "Hello world!";
            TextMessage message = session.createTextMessage(text);

            // Tell the producer to send the message
            if (t instanceof ActiveMQQueue) {
                System.out.println("Sent message: " + message.hashCode() + " to queue");
            } else {
                System.out.println("Sent message: " + message.hashCode() + " to topic");
            }

            producer.send(message);

            // Clean up
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void runQueue() {
        run(activeMQQueue);
    }

    public void runTopic() {
        run(activeMQTopic);
    }
}
