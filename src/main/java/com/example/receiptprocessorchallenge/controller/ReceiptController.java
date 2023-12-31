package com.example.receiptprocessorchallenge.controller;

import com.example.receiptprocessorchallenge.model.PointsResponse;
import com.example.receiptprocessorchallenge.model.Receipt;
import com.example.receiptprocessorchallenge.model.ReceiptResponse;
import com.example.receiptprocessorchallenge.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
public class ReceiptController {
    private ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    /**
     * Post endpoint that takes in a JSON receipt
     * and returns a response object with a generated ID
     * code.
     * Uses @Valid to validate Receipt request, return 400 response
     * with error message when fields are missing from Receipt
     * @param receipt
     * @return ResponseEntity
     */

    @PostMapping("/receipts/process")
    public ResponseEntity<ReceiptResponse> processReceipt(@Valid @RequestBody Receipt receipt) {

        // Return the response with HTTP status 200
        return ResponseEntity.ok(receiptService.processReceipt(receipt));
    }

    /**
     * Getter endpoint that looks up the receipt by the ID
     * and returns an object specifying the points awarded
     * @param id
     * @return ResponseEntity
     */
    @GetMapping("/receipts/{id}/points")
    public ResponseEntity<PointsResponse> getReceiptPoints(@PathVariable String id) {

        PointsResponse response = receiptService.getReceiptPoints(id);
        // Return the response with HTTP status 200
        if(response != null)
            return ResponseEntity.ok(response);
        // If no receipt found, return 404 response
        return ResponseEntity.notFound().build();
    }

}
