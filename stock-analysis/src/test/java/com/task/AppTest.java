package com.task;

import com.opencsv.exceptions.CsvException;
import com.task.model.StockData;
import com.task.service.DataService;
import com.task.service.MetricsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AppTest {

    @Autowired
    private DataService dataService;

    @Autowired
    private MetricsService metricsService;

    @Test
    public void contextLoads() {
        // Test to ensure the Spring application context loads correctly
        assertNotNull(dataService);
        assertNotNull(metricsService);
    }

    @Test
    public void testDataServiceLoadCSVData() throws IOException, CsvException {
        // Load the test CSV file from src/test/resources
        URL resource = getClass().getClassLoader().getResource("scandi.csv");
        if (resource == null) {
            throw new IOException("File not found: scandi.csv");
        }
        String filePath = Paths.get(resource.getPath()).toString();
        
        // Load CSV data and verify it is not null or empty
        List<StockData> data = dataService.loadCSVData(filePath);
        assertNotNull(data);
        assertTrue(data.size() > 0);
    }

    @Test
    public void testMetricsServiceCalculateMetrics() throws IOException, CsvException {
        // Load the test CSV file from src/test/resources
        URL resource = getClass().getClassLoader().getResource("scandi.csv");
        if (resource == null) {
            throw new IOException("File not found: scandi.csv");
        }
        String filePath = Paths.get(resource.getPath()).toString();
        
        // Calculate metrics and verify no exceptions are thrown
        metricsService.calculateMetrics(filePath);
    }
}
