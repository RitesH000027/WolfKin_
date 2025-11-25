package com.example.ecom.controller;

import com.example.ecom.service.DataInitializationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DataController {

    private final DataInitializationService dataInitializationService;

    @PostMapping("/initialize")
    @Operation(summary = "Initialize sample data", description = "Manually trigger sample data initialization")
    public ResponseEntity<String> initializeData() {
        try {
            dataInitializationService.initializeData();
            return ResponseEntity.ok("Sample data initialized successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error initializing data: " + e.getMessage());
        }
    }
}