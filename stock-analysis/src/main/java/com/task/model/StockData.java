package com.task.model;

public class StockData {
    private String bloombergCode;
    private double bidPrice;
    private double askPrice;
    private double tradePrice;
    private int bidVolume;
    private int askVolume;
    private int tradeVolume;
    private int updateType;
    private String date;
    private double timeInSecondsPastMidnight;
    private String conditionCodes;

    // Getters and setters
    public String getBloombergCode() { return bloombergCode; }
    public void setBloombergCode(String bloombergCode) { this.bloombergCode = bloombergCode; }

    public double getBidPrice() { return bidPrice; }
    public void setBidPrice(double bidPrice) { this.bidPrice = bidPrice; }

    public double getAskPrice() { return askPrice; }
    public void setAskPrice(double askPrice) { this.askPrice = askPrice; }

    public double getTradePrice() { return tradePrice; }
    public void setTradePrice(double tradePrice) { this.tradePrice = tradePrice; }

    public int getBidVolume() { return bidVolume; }
    public void setBidVolume(int bidVolume) { this.bidVolume = bidVolume; }

    public int getAskVolume() { return askVolume; }
    public void setAskVolume(int askVolume) { this.askVolume = askVolume; }

    public int getTradeVolume() { return tradeVolume; }
    public void setTradeVolume(int tradeVolume) { this.tradeVolume = tradeVolume; }

    public int getUpdateType() { return updateType; }
    public void setUpdateType(int updateType) { this.updateType = updateType; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getTimeInSecondsPastMidnight() { return timeInSecondsPastMidnight; }
    public void setTimeInSecondsPastMidnight(double timeInSecondsPastMidnight) { this.timeInSecondsPastMidnight = timeInSecondsPastMidnight; }

    public String getConditionCodes() { return conditionCodes; }
    public void setConditionCodes(String conditionCodes) { this.conditionCodes = conditionCodes; }
}
