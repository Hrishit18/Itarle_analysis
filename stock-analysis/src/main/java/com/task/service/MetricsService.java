package com.task.service;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.task.model.StockData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    @Autowired
    private DataService dataService;

    private List<String[]> metricsData;

    public MetricsService() {
        this.metricsData = new ArrayList<>();
        // Add CSV header
        this.metricsData.add(new String[]{
                "Stock Code", "Metric", "Value"
        });
    }

    /**
     * Calculates and prints all required metrics for the given CSV file.
     *
     * @param filePath The path to the CSV file.
     * @throws IOException If there is an issue reading the file.
     * @throws CsvException If there is an issue parsing the CSV file.
     */
    public void calculateMetrics(String filePath) throws IOException, CsvException {
        List<StockData> data = dataService.loadCSVData(filePath);
        data = dataService.filterAuctionPeriods(data);
        data = dataService.filterByConditionCode(data);
        data = dataService.sortDataByDateTime(data);

        // Group data by stock identifier
        Map<String, List<StockData>> groupedData = data.stream()
                .collect(Collectors.groupingBy(StockData::getBloombergCode));

        // Calculate metrics for each stock
        for (Map.Entry<String, List<StockData>> entry : groupedData.entrySet()) {
            String stockCode = entry.getKey();
            List<StockData> stockData = entry.getValue();

            calculateMeanTimeBetweenTrades(stockCode, stockData);
            calculateMedianTimeBetweenTrades(stockCode, stockData);
            calculateLongestTimeBetweenTrades(stockCode, stockData);
            calculateMeanTimeBetweenTickChanges(stockCode, stockData);
            calculateMedianTimeBetweenTickChanges(stockCode, stockData);
            calculateLongestTimeBetweenTickChanges(stockCode, stockData);
            calculateMeanBidAskSpread(stockCode, stockData);
            calculateMedianBidAskSpread(stockCode, stockData);
            analyzeRoundNumberEffect(stockCode, stockData);
        }

        // Correct file path to store the output in analysis.csv
        writeMetricsToCSV("../analysis.csv");
    }

    private void calculateMeanTimeBetweenTrades(String stockCode, List<StockData> data) {
        // function to calculate Mean Time Between Trades
        List<StockData> trades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .sorted(Comparator.comparing(StockData::getDate)
                        .thenComparing(StockData::getTimeInSecondsPastMidnight))
                .collect(Collectors.toList());

        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < trades.size(); i++) {
            Double timeDifference = trades.get(i).getTimeInSecondsPastMidnight() - trades.get(i - 1).getTimeInSecondsPastMidnight();
            // System.out.println(data.get(i).getTimeInSecondsPastMidnight()+ " " + data.get(i-1).getTimeInSecondsPastMidnight()+ " " + timeDifference);
            if(timeDifference > 0.0){
                timeDifferences.add(timeDifference);
            }
        }

        double meanTimeBetweenTrades = timeDifferences.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        metricsData.add(new String[]{stockCode, "Mean Time Between Trades", String.valueOf(meanTimeBetweenTrades)});
    }

    private void calculateMedianTimeBetweenTrades(String stockCode, List<StockData> data) {
        // function to calculate Median Time Between Trades
        List<StockData> trades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .sorted(Comparator.comparing(StockData::getDate)
                        .thenComparing(StockData::getTimeInSecondsPastMidnight))
                .collect(Collectors.toList());

        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < trades.size(); i++) {
            Double timeDifference = trades.get(i).getTimeInSecondsPastMidnight() - trades.get(i - 1).getTimeInSecondsPastMidnight();
            if(timeDifference > 0.0){
                timeDifferences.add(timeDifference);
            }
        }

        double medianTimeBetweenTrades = calculateMedianDouble(timeDifferences);
        metricsData.add(new String[]{stockCode, "Median Time Between Trades", String.valueOf(medianTimeBetweenTrades)});
    }

    private void calculateLongestTimeBetweenTrades(String stockCode, List<StockData> data) {
        // function to calculate Longest Time Between Trades
        List<StockData> trades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .sorted(Comparator.comparing(StockData::getDate)
                        .thenComparing(StockData::getTimeInSecondsPastMidnight))
                .collect(Collectors.toList());

        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < trades.size(); i++) {
            double timeDifference = trades.get(i).getTimeInSecondsPastMidnight() - trades.get(i - 1).getTimeInSecondsPastMidnight();
            if(timeDifference > 0.0){
                timeDifferences.add(timeDifference);
            }
        }

        double longestTimeBetweenTrades = timeDifferences.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        metricsData.add(new String[]{stockCode, "Longest Time Between Trades", String.valueOf(longestTimeBetweenTrades)});
    }

    private void calculateMeanTimeBetweenTickChanges(String stockCode, List<StockData> data) {
        // function to calculate Mean Time Between Tick Changes
        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            if(Math.abs(data.get(i).getTradePrice() - data.get(i-1).getTradePrice()) >= 0.1){
                double timeDifference = data.get(i).getTimeInSecondsPastMidnight() - data.get(i - 1).getTimeInSecondsPastMidnight();
                if(timeDifference > 0.0){
                    timeDifferences.add(timeDifference);
                }
            }
        }

        double meanTimeBetweenTickChanges = timeDifferences.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        metricsData.add(new String[]{stockCode, "Mean Time Between Tick Changes", String.valueOf(meanTimeBetweenTickChanges)});
    }

    private void calculateMedianTimeBetweenTickChanges(String stockCode, List<StockData> data) {
        // function to calculate Median Time Between Tick Changes
        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            if(Math.abs(data.get(i).getTradePrice() - data.get(i-1).getTradePrice()) >= 0.1){
                double timeDifference = data.get(i).getTimeInSecondsPastMidnight() - data.get(i - 1).getTimeInSecondsPastMidnight();
                if(timeDifference > 0.0){
                    timeDifferences.add(timeDifference);
                }
            }
        }

        double medianTimeBetweenTickChanges = calculateMedianDouble(timeDifferences);
        metricsData.add(new String[]{stockCode, "Median Time Between Tick Changes", String.valueOf(medianTimeBetweenTickChanges)});
    }

    private void calculateLongestTimeBetweenTickChanges(String stockCode, List<StockData> data) {
        // function to calculate Longest Time Between Tick Changes
        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            if(Math.abs(data.get(i).getTradePrice() - data.get(i-1).getTradePrice()) >= 0.1){
                double timeDifference = data.get(i).getTimeInSecondsPastMidnight() - data.get(i - 1).getTimeInSecondsPastMidnight();
                timeDifferences.add(timeDifference);
            }
        }

        double longestTimeBetweenTickChanges = timeDifferences.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        metricsData.add(new String[]{stockCode, "Longest Time Between Tick Changes", String.valueOf(longestTimeBetweenTickChanges)});
    }

    private void calculateMeanBidAskSpread(String stockCode, List<StockData> data) {
        // function to calculate Mean Bid Ask Spread
        List<Double> spreads = data.stream()
                .filter(d -> d.getBidPrice() > 0 && d.getAskPrice() > 0)
                .map(d -> d.getAskPrice() - d.getBidPrice())
                .collect(Collectors.toList());

        double meanBidAskSpread = spreads.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        metricsData.add(new String[]{stockCode, "Mean Bid-Ask Spread", String.valueOf(meanBidAskSpread)});
    }

    private void calculateMedianBidAskSpread(String stockCode, List<StockData> data) {
        // function to calculate Median Bid Ask Spread
        List<Double> spreads = data.stream()
                .filter(d -> d.getBidPrice() > 0 && d.getAskPrice() > 0)
                .map(d -> d.getAskPrice() - d.getBidPrice())
                .collect(Collectors.toList());

        double medianBidAskSpread = calculateMedianDouble(spreads);
        metricsData.add(new String[]{stockCode, "Median Bid-Ask Spread", String.valueOf(medianBidAskSpread)});
    }

    private void analyzeRoundNumberEffect(String stockCode, List<StockData> data) {
        // function to analyze Round Number Effect
        long roundNumberTrades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .filter(d -> d.getTradePrice() % 10 == 0)
                .count();

        long totalTrades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .count();

        double roundNumberTradePercentage = ((double) roundNumberTrades / totalTrades) * 100;
        metricsData.add(new String[]{stockCode, "Round Number Effect in Trade Prices", roundNumberTradePercentage + "%"});

        long roundNumberVolumes = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .filter(d -> d.getTradeVolume() % 10 == 0)
                .count();

        long totalVolumes = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .count();

        double roundNumberVolumePercentage = ((double) roundNumberVolumes / totalVolumes) * 100;
        metricsData.add(new String[]{stockCode, "Round Number Effect in Trade Volumes", roundNumberVolumePercentage + "%"});
    }

    private double calculateMedianDouble(List<Double> values) {
        // function to calculate the Median value given a list of (double) values
        int size = values.size();
        if (size == 0) {
            return 0;
        }
        List<Double> sortedValues = values.stream().sorted().collect(Collectors.toList());
        if (size % 2 == 1) {
            return sortedValues.get(size / 2);
        } else {
            return (sortedValues.get((size / 2) - 1) + sortedValues.get(size / 2)) / 2.0;
        }
    }

    /**
     * Writes the metrics data to a CSV file.
     *
     * @param outputPath The path to the output CSV file.
     * @throws IOException If there is an issue writing to the file.
     */
    private void writeMetricsToCSV(String outputPath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath))) {
            writer.writeAll(metricsData);
        }
    }
}
