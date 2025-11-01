package com.example.demo.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.service.StoreLineService;

@Component
public class MQListener {

    @Autowired
    private StoreLineService service;

    @JmsListener(destination = "${ibm.mq.queue.name}")
    public void receiveMessage(String message) {
        try {
            System.out.println("✅ Received message from IBM MQ: " + message);
            service.processIncomingXml(message);
        } catch (Exception e) {
            System.err.println("❌ Error processing MQ message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
