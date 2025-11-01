package com.example.demo.consumer;

import com.example.demo.service.StoreLineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class MQListenerTest {

    @Mock
    private StoreLineService service;

    @InjectMocks
    private MQListener listener;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveMessage_Success() {
        String xml = "<Store><Id>1</Id></Store>";

        doNothing().when(service).processIncomingXml(xml);
        listener.receiveMessage(xml);

        verify(service, times(1)).processIncomingXml(xml);
    }

    @Test
    void testReceiveMessage_ExceptionHandled() {
        String xml = "<Store><Id>1</Id></Store>";
        doThrow(new RuntimeException("Processing failed")).when(service).processIncomingXml(xml);

        listener.receiveMessage(xml);

        verify(service, times(1)).processIncomingXml(xml);
    }
}
