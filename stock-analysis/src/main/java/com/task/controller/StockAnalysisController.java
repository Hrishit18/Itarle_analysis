package com.task.controller;

import com.opencsv.exceptions.CsvException;
import com.task.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class StockAnalysisController {

    @Autowired
    private MetricsService metricsService;

    /**
     * Endpoint to analyze stock data from the given CSV file.
     * 
     * @param filePath The path to the CSV file.
     * @return A message indicating whether the analysis was successful or if an error occurred.
     */
    @GetMapping("/analyze")
    public String analyzeStockData(@RequestParam String filePath) {
        try {
            metricsService.calculateMetrics(filePath);
            return "Analysis completed. Check the report.";
        } catch (IOException | CsvException e) {
            return "Error processing the file: " + e.getMessage();
        }
    }
}
