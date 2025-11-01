package com.example.demo.service;

import com.example.demo.model.EventLog;
import com.example.demo.producer.StorelineKafkaProducer;
import com.example.demo.repository.EventLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StoreLineServiceTest {

    @Mock
    private StorelineKafkaProducer kafkaProducer;

    @Mock
    private EventLogRepository eventLogRepository;

    @InjectMocks
    private StoreLineService storeLineService;

    private final String validXml = "<Store><Id>1001</Id><Name>ABC</Name></Store>";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Optionally silence logger in tests by setting level programmatically (if needed)
    }

    @Test
    void testProcessIncomingXml_Success() {
        // Arrange
        doNothing().when(kafkaProducer).sendToKafka(anyString(), any(EventLog.class));
        when(eventLogRepository.save(any(EventLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        storeLineService.processIncomingXml(validXml);

        // Assert: Kafka should be called twice, and save called once
        verify(kafkaProducer, times(2)).sendToKafka(anyString(), any(EventLog.class));
        verify(eventLogRepository, times(1)).save(any(EventLog.class));
    }

    @Test
    void testProcessIncomingXml_EmptyXml_NoInteractions() {
        // Act
        storeLineService.processIncomingXml("   ");

        // Assert: nothing should be called
        verifyNoInteractions(kafkaProducer);
        verifyNoInteractions(eventLogRepository);
    }

    @Test
    void testProcessIncomingXml_KafkaFails_ButMongoSucceeds() {
        // Arrange: Kafka fails
        doThrow(new RuntimeException("Kafka failure")).when(kafkaProducer)
                .sendToKafka(anyString(), any(EventLog.class));
        // Mongo succeeds and returns the EventLog
        when(eventLogRepository.save(any(EventLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        storeLineService.processIncomingXml(validXml);

        // Assert: Kafka attempted twice (and threw), Mongo still called once
        verify(kafkaProducer, times(2)).sendToKafka(anyString(), any(EventLog.class));
        verify(eventLogRepository, times(1)).save(any(EventLog.class));
    }

    @Test
    void testProcessIncomingXml_MongoFails() {
        // Arrange
        doNothing().when(kafkaProducer).sendToKafka(anyString(), any(EventLog.class));
        doThrow(new RuntimeException("Mongo failure")).when(eventLogRepository).save(any(EventLog.class));

        // Act
        storeLineService.processIncomingXml(validXml);

        // Assert: kafka called twice and mongo tried once (but failed)
        verify(kafkaProducer, times(2)).sendToKafka(anyString(), any(EventLog.class));
        verify(eventLogRepository, times(1)).save(any(EventLog.class));
    }
}
