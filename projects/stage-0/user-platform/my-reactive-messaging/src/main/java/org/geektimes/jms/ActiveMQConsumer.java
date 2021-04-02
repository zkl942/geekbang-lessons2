package org.geektimes.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * note: Topics don't retain messages
 */
public class ActiveMQConsumer implements ExceptionListener {

    @Resource(name = "jms/ActiveMQConnectionFactory")
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @Resource(name = "jms/queue/MyQueue")
    private ActiveMQQueue activeMQQueue;

    @Resource(name = "jms/topic/MyTopic")
    private ActiveMQTopic activeMQTopic;

    private MessageConsumer consumer = null;
    private Session session = null;
    private Connection connection = null;

    public <T extends ActiveMQDestination> void receive(T t) {
        try {
            // in case /hello/activemq is called again when consumer is already initialized
            if (consumer != null && session != null && connection != null) {
                return;
            }

            // Create a Connection
            connection = activeMQConnectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = t;

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(destination);

            // onMessage is run in another thread
            MessageListener messageListener = message -> {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();
                        if (t instanceof ActiveMQQueue) {
                            System.out.println("Received message: " + text + " from queue");
                        } else {
                            System.out.println("Received message: " + text + " from topic");
                        }
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            };
            consumer.setMessageListener(messageListener);
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            // If this method is called whilst a message listener is in progress in another thread then
            // it will block until the message listener has completed.
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }

    public void receiveFromQueue() {
        receive(activeMQQueue);
    }

    public void receiveFromTopic() {
        receive(activeMQTopic);
    }
}
