package com.task.service;

import com.opencsv.exceptions.CsvException;
import com.task.model.StockData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            System.out.println("Metrics for stock: " + stockCode);
            calculateMeanTimeBetweenTrades(stockData);
            calculateMedianTimeBetweenTrades(stockData);
            calculateLongestTimeBetweenTrades(stockData);
            calculateMeanTimeBetweenTickChanges(stockData);
            calculateMedianTimeBetweenTickChanges(stockData);
            calculateLongestTimeBetweenTickChanges(stockData);
            calculateMeanBidAskSpread(stockData);
            calculateMedianBidAskSpread(stockData);
            analyzeRoundNumberEffect(stockData);
            System.out.println();
        }
    }

    private void calculateMeanTimeBetweenTrades(List<StockData> data) {
        List<StockData> trades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .sorted(Comparator.comparing(StockData::getDate)
                        .thenComparing(StockData::getTimeInSecondsPastMidnight))
                .collect(Collectors.toList());

        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < trades.size(); i++) {
            Double timeDifference = trades.get(i).getTimeInSecondsPastMidnight() - trades.get(i - 1).getTimeInSecondsPastMidnight();
            timeDifferences.add(timeDifference);
        }

        double meanTimeBetweenTrades = timeDifferences.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        System.out.println("Mean Time Between Trades: " + meanTimeBetweenTrades);
    }

    private void calculateMedianTimeBetweenTrades(List<StockData> data) {
        List<StockData> trades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .sorted(Comparator.comparing(StockData::getDate)
                        .thenComparing(StockData::getTimeInSecondsPastMidnight))
                .collect(Collectors.toList());

        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < trades.size(); i++) {
            Double timeDifference = trades.get(i).getTimeInSecondsPastMidnight() - trades.get(i - 1).getTimeInSecondsPastMidnight();
            timeDifferences.add(timeDifference);
        }

        double medianTimeBetweenTrades = calculateMedianDouble(timeDifferences);
        System.out.println("Median Time Between Trades: " + medianTimeBetweenTrades);
    }

    private void calculateLongestTimeBetweenTrades(List<StockData> data) {
        List<StockData> trades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .sorted(Comparator.comparing(StockData::getDate)
                        .thenComparing(StockData::getTimeInSecondsPastMidnight))
                .collect(Collectors.toList());

        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < trades.size(); i++) {
            double timeDifference = trades.get(i).getTimeInSecondsPastMidnight() - trades.get(i - 1).getTimeInSecondsPastMidnight();
            timeDifferences.add(timeDifference);
        }

        double longestTimeBetweenTrades = timeDifferences.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        System.out.println("Longest Time Between Trades: " + longestTimeBetweenTrades);
    }

    private void calculateMeanTimeBetweenTickChanges(List<StockData> data) {
        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            double timeDifference = data.get(i).getTimeInSecondsPastMidnight() - data.get(i - 1).getTimeInSecondsPastMidnight();
            timeDifferences.add(timeDifference);
        }

        double meanTimeBetweenTickChanges = timeDifferences.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        System.out.println("Mean Time Between Tick Changes: " + meanTimeBetweenTickChanges);
    }

    private void calculateMedianTimeBetweenTickChanges(List<StockData> data) {
        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            double timeDifference = data.get(i).getTimeInSecondsPastMidnight() - data.get(i - 1).getTimeInSecondsPastMidnight();
            timeDifferences.add(timeDifference);
        }

        double medianTimeBetweenTickChanges = calculateMedianDouble(timeDifferences);
        System.out.println("Median Time Between Tick Changes: " + medianTimeBetweenTickChanges);
    }

    private void calculateLongestTimeBetweenTickChanges(List<StockData> data) {
        List<Double> timeDifferences = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            double timeDifference = data.get(i).getTimeInSecondsPastMidnight() - data.get(i - 1).getTimeInSecondsPastMidnight();
            timeDifferences.add(timeDifference);
        }

        double longestTimeBetweenTickChanges = timeDifferences.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        System.out.println("Longest Time Between Tick Changes: " + longestTimeBetweenTickChanges);
    }

    private void calculateMeanBidAskSpread(List<StockData> data) {
        List<Double> spreads = data.stream()
                .filter(d -> d.getBidPrice() > 0 && d.getAskPrice() > 0)
                .map(d -> d.getAskPrice() - d.getBidPrice())
                .collect(Collectors.toList());

        double meanBidAskSpread = spreads.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        System.out.println("Mean Bid-Ask Spread: " + meanBidAskSpread);
    }

    private void calculateMedianBidAskSpread(List<StockData> data) {
        List<Double> spreads = data.stream()
                .filter(d -> d.getBidPrice() > 0 && d.getAskPrice() > 0)
                .map(d -> d.getAskPrice() - d.getBidPrice())
                .collect(Collectors.toList());

        double medianBidAskSpread = calculateMedianDouble(spreads);
        System.out.println("Median Bid-Ask Spread: " + medianBidAskSpread);
    }

    private void analyzeRoundNumberEffect(List<StockData> data) {
        long roundNumberTrades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .filter(d -> d.getTradePrice() % 10 == 0)
                .count();

        long totalTrades = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .count();

        double roundNumberTradePercentage = ((double) roundNumberTrades / totalTrades) * 100;
        System.out.println("Round Number Effect in Trade Prices: " + roundNumberTradePercentage + "%");

        long roundNumberVolumes = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .filter(d -> d.getTradeVolume() % 10 == 0)
                .count();

        long totalVolumes = data.stream()
                .filter(d -> d.getUpdateType() == 1)
                .count();

        double roundNumberVolumePercentage = ((double) roundNumberVolumes / totalVolumes) * 100;
        System.out.println("Round Number Effect in Trade Volumes: " + roundNumberVolumePercentage + "%");
    }

    private double calculateMedianDouble(List<Double> values) {
        int size = values.size();
        if (size == 0) {
            return 0;
        }
        List<Double> sortedValues = values.stream().sorted().collect(Collectors.toList());
        if (size % 2 == 1) {
            return sortedValues.get(size / 2);
        } else {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        }
    }
}
