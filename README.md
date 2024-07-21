# Itarle_analysis

## Description
Itarle Analysis is a Java-based project that provides tools for analyzing stock market data. The project includes services for loading, processing, and analyzing stock market data from CSV files. It utilizes statistical methods to compute various metrics such as median bid-ask spreads and round number effects on trade prices and volumes.

## Features
•⁠  ⁠*Data Loading*: Load and parse stock market data from CSV files into Java objects.
•⁠  ⁠*Metrics Calculation*: Calculate median bid-ask spreads and analyze the effect of round numbers on trade prices and volumes.

## Getting Started

### Prerequisites
•⁠ ⁠Java 11 or higher
•⁠ Maven
• Spring Boot
• Java
• OpenCSV

### Installation
1.⁠ ⁠Clone the repository:
   git clone https://github.com/Hrishit18/Itarle_analysis.git
2. Running the code:
    First, navigate to the root directory in your terminal
    Next run the following commands:
    cd Itarle_analysis/stock-analysis
    mvn clean install
    mvn spring-boot:run
3. Tests:
    This project runs the tests when you run `mvn clean install'. This is contained in the AppTest class. Here we check the following things:
    • Verifying that the Spring application context loads correctly.
    • Testing the loading and parsing of CSV data.
    • Testing the calculation of metrics without throwing exceptions.



