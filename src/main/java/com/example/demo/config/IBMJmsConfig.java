package com.example.demo.config;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * IBM MQ JMS Configuration (Enterprise-ready)
 * Connects Spring Boot to IBM MQ Queue Manager (local/Docker or remote).
 */
@Configuration
public class IBMJmsConfig {

    @Value("${ibm.mq.queueManager}")
    private String queueManager;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.connName}")
    private String connName;

    @Value("${ibm.mq.user}")
    private String user;

    @Value("${ibm.mq.password}")
    private String password;

    /**
     * Configures MQ JMS ConnectionFactory with authentication and reliability tuning.
     */
    @Bean
    public ConnectionFactory mqConnectionFactory() throws JMSException {
        MQConnectionFactory factory = new MQConnectionFactory();

        factory.setQueueManager(queueManager);
        factory.setChannel(channel);
        factory.setConnectionNameList(connName);
        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);

        factory.setUserAuthenticationMQCSP(true);
        factory.setStringProperty(WMQConstants.USERID, user);
        factory.setStringProperty(WMQConstants.PASSWORD, password);

        System.out.printf("""
                ✅ IBM MQ Connection Configured:
                   QueueManager: %s
                   Channel: %s
                   Connection: %s
                   User: %s
                """, queueManager, channel, connName, user);

        return factory;
    }
}
