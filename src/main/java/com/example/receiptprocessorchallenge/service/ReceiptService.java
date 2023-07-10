package com.example.receiptprocessorchallenge.service;

import com.example.receiptprocessorchallenge.model.Item;
import com.example.receiptprocessorchallenge.model.PointsResponse;
import com.example.receiptprocessorchallenge.model.Receipt;
import com.example.receiptprocessorchallenge.model.ReceiptResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReceiptService {
    private Map<String, Receipt> receiptMap = new HashMap<>();

    /**
     * Return a unique generated ID for the receipt
     * and store the receipt in the map using the generated ID as the key
     * @param receipt
     * @return ReceiptResponse
     */
    public ReceiptResponse processReceipt(Receipt receipt) {
        // Generate a unique ID for the receipt
        String id = UUID.randomUUID().toString();

        // Create the response object
        ReceiptResponse response = new ReceiptResponse(id);

        // Store the receipt in the map using the generated ID as the key
        receiptMap.put(id, receipt);

        return response;
    }

    /**
     * Lookup the receipt by ID from the map and calculate the points
     * based on the receipt rules and create the response object
     * @param id
     * @return PointsResponse
     */
    public PointsResponse getReceiptPoints(String id) {
        // Lookup the receipt by ID from the map
        Receipt receipt = receiptMap.get(id);

        // If no receipt found for the given ID, return null response
        if (receipt == null) {
            return null;
        }

        // Calculate the points based on the receipt and create the response object
        int points = calculatePoints(receipt);
        PointsResponse response = new PointsResponse(points);

        return response;
    }

    /**
     * Calculate pints for the receipt based on requirements
     * @param receipt
     * @return int points
     */
    private int calculatePoints(Receipt receipt) {
        int points = 0;

        // Rule: One point for every alphanumeric character in the retailer name
       points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        // Rule: 50 points if the total is a round dollar amount with no cents
        double total = Double.parseDouble(receipt.getTotal());
        if (total == (int) total) {
            points += 50;
        }

        // Rule: 25 points if the total is a multiple of 0.25
        if (total % 0.25 == 0) {
            points += 25;
        }

        // Rule: 5 points for every two items on the receipt
        int itemSize = receipt.getItems().size();
        points += (itemSize / 2) * 5;

        // Rule: If the trimmed length of the item description is a multiple of 3,
        // multiply the price by 0.2 and round up to the nearest integer.
        // The result is the number of points earned.
        for (Item item : receipt.getItems()) {
            String description = item.getShortDescription().trim();
            if (description.length() % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                int itemPoints = (int) Math.ceil(price * 0.2);
                points += itemPoints;
            }
        }

        // Rule: 6 points if the day in the purchase date is odd
        int purchaseDay = Integer.parseInt(receipt.getPurchaseDate().substring(8));
        if (purchaseDay % 2 != 0) {
            points += 6;
        }

        // Rule: 10 points if the time of purchase is after 2:00pm and before 4:00pm
        String purchaseTime = receipt.getPurchaseTime();
        int hour = Integer.parseInt(purchaseTime.substring(0, 2));
        int minutes = Integer.parseInt(purchaseTime.substring(3, 5));
        if ((hour == 14 && minutes > 0 || hour > 14) && hour < 16) {
            points += 10;
        }

        return points;
    }
}


