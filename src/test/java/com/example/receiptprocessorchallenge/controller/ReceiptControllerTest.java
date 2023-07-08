package com.example.receiptprocessorchallenge.controller;

import com.example.receiptprocessorchallenge.model.Item;
import com.example.receiptprocessorchallenge.model.PointsResponse;
import com.example.receiptprocessorchallenge.model.Receipt;
import com.example.receiptprocessorchallenge.model.ReceiptResponse;
import com.example.receiptprocessorchallenge.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReceiptControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReceiptController receiptController = new ReceiptController(receiptService);
        mockMvc = MockMvcBuilders.standaloneSetup(receiptController).build();
    }

    @Test
    void testProcessReceipt_callService_return200() throws Exception {
        Receipt receipt = createTestReceipt();
        ReceiptResponse expectedResponse = new ReceiptResponse("7fb1377b-b223-49d9-a31a-5a02701dd310");
        when(receiptService.processReceipt(any(Receipt.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"retailer\":\"Target\",\"purchaseDate\":\"2022-01-01\",\"purchaseTime\":\"13:01\",\"items\":[{\"shortDescription\":\"Mountain Dew 12PK\",\"price\":\"6.49\"}],\"total\":\"6.49\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("7fb1377b-b223-49d9-a31a-5a02701dd310"));
    }

    @Test
    void testProcessReceipt_return400() throws Exception {
        Receipt receipt = createTestReceipt();
        ReceiptResponse expectedResponse = new ReceiptResponse("7fb1377b-b223-49d9-a31a-5a02701dd310");
        when(receiptService.processReceipt(any(Receipt.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"purchaseDate\":\"2022-01-01\",\"purchaseTime\":\"13:01\",\"items\":[{\"shortDescription\":\"Mountain Dew 12PK\",\"price\":\"6.49\"}],\"total\":\"6.49\"}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testGetReceiptPoints_callService_return200() throws Exception {
        String receiptId = "7fb1377b-b223-49d9-a31a-5a02701dd310";
        PointsResponse expectedResponse = new PointsResponse(12);
        when(receiptService.getReceiptPoints(eq(receiptId))).thenReturn(expectedResponse);

        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.points").value(12));
    }

    @Test
    void testGetReceiptPoints_return404_when_receipt_id_not_found() throws Exception {
        String receiptId = "7fb1377b-b223-49d9-a31a-5a02701dd310";

        when(receiptService.getReceiptPoints(anyString())).thenReturn(null);

        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isNotFound());
    }

    private Receipt createTestReceipt() {
        Receipt receipt = Receipt.builder()
                .retailer("Target")
                .total("6.49")
                .purchaseDate("2022-01-01")
                .purchaseTime("13:01")
                .items(List.of(Item.builder()
                        .shortDescription("Mountain Dew 12PK")
                        .price("6.49").build()))
                .build();
        return receipt;
    }
}
