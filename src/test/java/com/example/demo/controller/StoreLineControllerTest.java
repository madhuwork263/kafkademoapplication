package com.example.demo.controller;

import com.example.demo.service.StoreLineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StoreLineController (Local)
 * ------------------------------------------
 * Verifies the REST endpoints using mocked StoreLineService.
 */
class StoreLineControllerTest {

    @Mock
    private StoreLineService storeLineService;

    @InjectMocks
    private StoreLineController storeLineController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendXml_Success() {
        // Given
        String xml = "<StoreLine><StoreId>1001</StoreId><StoreName>ABC SuperMart</StoreName></StoreLine>";
        doNothing().when(storeLineService).processIncomingXml(xml);

        // When
        ResponseEntity<String> response = storeLineController.sendXml(xml);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("✅ XML processed successfully"));
        verify(storeLineService, times(1)).processIncomingXml(xml);
    }

    @Test
    void testSendXml_Failure() {
        // Given
        String xml = "<StoreLine><StoreId>1001</StoreId></StoreLine>";
        doThrow(new RuntimeException("Kafka down")).when(storeLineService).processIncomingXml(xml);

        // When
        ResponseEntity<String> response = storeLineController.sendXml(xml);

        // Then
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Failed to process XML"));
        verify(storeLineService, times(1)).processIncomingXml(xml);
    }

    @Test
    void testHealthCheck() {
        // When
        ResponseEntity<String> response = storeLineController.health();

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("StoreLine API is running ✅", response.getBody());
    }
}
