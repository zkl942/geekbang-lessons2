package org.geektimes.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.annotation.Resource;
import javax.jms.*;

public class ActiveMQConsumer implements ExceptionListener {

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

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = t;

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // Wait for a message
            Message message = consumer.receive(1000);

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("Received: " + text);
            } else {
                System.out.println("Received: " + message);
            }

            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }

    public void runQueue() {
        run(activeMQQueue);
    }

    public void runTopic() {
        run(activeMQTopic);
    }
}
