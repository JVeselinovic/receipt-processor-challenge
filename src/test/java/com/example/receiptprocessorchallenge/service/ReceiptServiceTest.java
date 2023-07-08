package com.example.receiptprocessorchallenge.service;

import com.example.receiptprocessorchallenge.model.Item;
import com.example.receiptprocessorchallenge.model.PointsResponse;
import com.example.receiptprocessorchallenge.model.Receipt;
import com.example.receiptprocessorchallenge.model.ReceiptResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReceiptServiceTest {

    private ReceiptService receiptService;
    private Receipt receipt;

    @BeforeEach
    void setUp() {
        receiptService = new ReceiptService();
        receipt = createTestReceipt();
    }

    @Test
    void testProcessReceipt_return_id() {
        ReceiptResponse receiptResponse = receiptService.processReceipt(receipt);
        assertNotNull(receiptResponse);
        assertNotNull(receiptResponse.getId());
    }

    @Test
    void testGetReceiptPoints_return_price() {
        ReceiptResponse receiptResponse = receiptService.processReceipt(receipt);
        PointsResponse response = receiptService.getReceiptPoints(receiptResponse.getId());

        assertNotNull(response);
        assertEquals(28, response.getPoints());
    }

    @Test
    void testGetReceiptPoints_return_null_for_not_found_id() {
        String receiptId = "7fb1377b-b223-49d9-a31a-5a02701dd310";
        PointsResponse response = receiptService.getReceiptPoints(receiptId);

        assertNull(response);
    }

    @Test
    void testGetReceiptPoints_return_price_with_round_total() {
        //change receipt total to be round dollar amount with no cents
        //add 50 points to the original total return price
        receipt.setTotal("35.00");
        ReceiptResponse receiptResponse = receiptService.processReceipt(receipt);
        PointsResponse response = receiptService.getReceiptPoints(receiptResponse.getId());
        assertEquals(103, response.getPoints());
    }

    @Test
    void testGetReceiptPoints_return_price_with_total_multiple_of025() {
        //change receipt total to be multiple_of 0.25
        //add 25 points to the original total return price
        receipt.setTotal("25.25");
        ReceiptResponse receiptResponse = receiptService.processReceipt(receipt);
        PointsResponse response = receiptService.getReceiptPoints(receiptResponse.getId());
        assertEquals(53, response.getPoints());
    }

    @Test
    void testGetReceiptPoints_return_price_with_number_of_items_changed() {
        //change the number of items
        //subtract 5 points to the original total return price
        // as now there are only two items on the receipt
        receipt.setItems(List.of(
                Item.builder()
                        .shortDescription("   Klarbrunn 12-PK 12 FL OZ  ")
                        .price("12.00").build(),
                Item.builder()
                        .shortDescription("Emils Cheese Pizza")
                        .price("12.25").build()));
        ReceiptResponse receiptResponse = receiptService.processReceipt(receipt);
        PointsResponse response = receiptService.getReceiptPoints(receiptResponse.getId());

        assertEquals(23, response.getPoints());
    }

    @Test
    void testGetReceiptPoints_return_price_with_item_description_changed() {
        //change the description of the second item
        // and the trimmed length of the item description is not
        // a multiple of 3,
        //to subtract 3 points to the original total return price
        receipt.getItems().get(1).setShortDescription("Emil Cheese Pizza");
        ReceiptResponse receiptResponse = receiptService.processReceipt(receipt);
        PointsResponse response = receiptService.getReceiptPoints(receiptResponse.getId());

        assertEquals(25, response.getPoints());
    }

    @Test
    void testGetReceiptPoints_return_price_with_purchase_day_even() {
        //change the time of purchase day to be even
        //to subtract 6 points to the original total return price
        receipt = createTestReceipt();
        receipt.setPurchaseDate("2022-01-06");
        ReceiptResponse receiptResponse = receiptService.processReceipt(receipt);
        PointsResponse response = receiptService.getReceiptPoints(receiptResponse.getId());

        assertEquals(22, response.getPoints());

    }

    @Test
    void testGetReceiptPoints_return_price_with_time_between_14_16() {
        //change the time of purchase is after 2:00pm and before 4:00pm
        // to add 10 points to the original total return price
        receipt.setPurchaseTime("14:01");
        ReceiptResponse receiptResponse = receiptService.processReceipt(receipt);
        PointsResponse response = receiptService.getReceiptPoints(receiptResponse.getId());

        assertEquals(38, response.getPoints());

    }

    private Receipt createTestReceipt() {
        Receipt receipt = Receipt.builder()
                .retailer("Target")
                .total("35.35")
                .purchaseDate("2022-01-01")
                .purchaseTime("13:01")
                .items(List.of(
                        Item.builder()
                                .shortDescription("Mountain Dew 12PK")
                                .price("6.49").build(),
                        Item.builder()
                                .shortDescription("Emils Cheese Pizza")
                                .price("12.25").build(),
                        Item.builder()
                                .shortDescription("Knorr Creamy Chicken")
                                .price("1.26").build(),
                        Item.builder()
                                .shortDescription("Doritos Nacho Cheese")
                                .price("3.35").build(),
                        Item.builder()
                                .shortDescription("   Klarbrunn 12-PK 12 FL OZ  ")
                                .price("12.00").build()))

                .build();
        return receipt;
    }
}