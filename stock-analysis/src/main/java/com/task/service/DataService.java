package com.task.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.task.model.StockData;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataService {

    /**
     * Loads CSV data from the specified file path.
     * 
     * @param filePath The path to the CSV file.
     * @return A list of StockData objects representing the data in the CSV file.
     * @throws IOException If there is an issue reading the file.
     * @throws CsvException If there is an issue parsing the CSV file.
     */
    public List<StockData> loadCSVData(String filePath) throws IOException, CsvException {
        List<StockData> stockDataList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                try {
                    StockData data = new StockData();
                    data.setBloombergCode(nextLine[0]);
                    data.setBidPrice(parseDouble(nextLine[2]));
                    data.setAskPrice(parseDouble(nextLine[3]));
                    data.setTradePrice(parseDouble(nextLine[4]));
                    data.setBidVolume(parseInt(nextLine[5]));
                    data.setAskVolume(parseInt(nextLine[6]));
                    data.setTradeVolume(parseInt(nextLine[7]));
                    data.setUpdateType(parseInt(nextLine[8]));
                    data.setDate(nextLine[10]);
                    data.setTimeInSecondsPastMidnight(parseDouble(nextLine[11]));
                    data.setConditionCodes(nextLine[14]);
                    stockDataList.add(data);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + String.join(",", nextLine));
                    e.printStackTrace();
                }
            }
        }
        return stockDataList;
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing double value: " + value);
            return 0.0; // Default value or handle error as needed
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing int value: " + value);
            return 0; // Default value or handle error as needed
        }
    }

    /**
     * Filters out auction periods based on specific condition codes and crossed spreads.
     * 
     * @param data The list of StockData objects to filter.
     * @return A filtered list of StockData objects excluding auction periods.
     */
    public List<StockData> filterAuctionPeriods(List<StockData> data) {
        return data.stream()
                .filter(d -> !isAuctionPeriod(d))
                .collect(Collectors.toList());
    }

    /**
     * Determines if a StockData entry is within an auction period based on specific condition codes and crossed spreads.
     * 
     * @param data The StockData object to check.
     * @return True if the entry is within an auction period, otherwise false.
     */
    private boolean isAuctionPeriod(StockData data) {
        // Implement the logic to determine if the data entry is within an auction period
        // This can be based on specific condition codes and crossed spreads
        // Example condition codes for auctions might be "AU" or similar; adjust as needed
        return data.getConditionCodes().equals("AU") || data.getBidPrice() > data.getAskPrice();
    }

    /**
     * Excludes entries without the 'XT' condition code or with no condition code.
     * 
     * @param data The list of StockData objects to filter.
     * @return A filtered list of StockData objects with only 'XT' condition code or no condition code.
     */
    public List<StockData> filterByConditionCode(List<StockData> data) {
        return data.stream()
                .filter(d -> d.getConditionCodes().equals("XT") || d.getConditionCodes().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Sorts the data by date and time in seconds past midnight.
     * 
     * @param data The list of StockData objects to sort.
     * @return A sorted list of StockData objects by date and time.
     */
    public List<StockData> sortDataByDateTime(List<StockData> data) {
        return data.stream()
                .sorted(Comparator.comparing(StockData::getDate)
                                  .thenComparing(StockData::getTimeInSecondsPastMidnight))
                .collect(Collectors.toList());
    }
}
