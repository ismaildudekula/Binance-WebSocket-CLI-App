Overview
This Java-based command-line application connects to Binance's WebSocket API to fetch real-time market depth data for BTC/USDT. It allows users to set buy and sell trigger prices, simulates placing orders when the trigger prices are hit, and cancels them if the price moves away from the prepared order price. This is a simulation, meaning that no real orders are sent to Binance, but the app logs the payloads as if orders were prepared.

Features
Real-time BTC/USDT best bid and ask price updates.
Simulates buy and sell order preparation when specific trigger prices are hit.
Cancels prepared orders if the price moves away from the triggered price.
Displays a clean, continuously updated console output with market data.
Uses the WebSocket protocol to stream data in real-time, providing low-latency price updates.
Prerequisites
Java 8 or above.
Maven (for dependency management).
Internet connection (to connect to Binance WebSocket API).
Setup Instructions
1. Clone the repository:
bash
Copy code
git clone https://github.com/yourusername/BinanceWebSocketClient.git
cd BinanceWebSocketClient
2. Build the application:
Use Maven to build the application:

bash
Copy code
mvn clean install
3. Run the application:
Once built, you can run the application using:

bash
Copy code
java -jar target/BinanceWebSocketClient.jar
4. Operation:
When the program starts, you will be prompted to enter:

A Buy Trigger Price (the price at which you want to simulate a buy order).
A Sell Trigger Price (the price at which you want to simulate a sell order).
The application will connect to the Binance WebSocket server and stream BTC/USDT price updates in real time.

When the best ask price reaches or drops below the buy trigger price, a simulated buy order will be prepared. If the price rises above the buy trigger, the order will be canceled.

When the best bid price reaches or rises above the sell trigger price, a simulated sell order will be prepared. If the price drops below the sell trigger, the order will be canceled.

5. Stop the application:
To stop the application, simply press Ctrl + C in your terminal or close the terminal window.

Design Decisions
1. WebSocket Client:
The application uses org.java_websocket.client.WebSocketClient from the Java-WebSocket library to connect to Binance's WebSocket API. This provides a lightweight and straightforward way to subscribe to real-time market depth data.

2. Real-time Market Data:
The application subscribes to the Binance btcusdt@depth stream to fetch the best bid and ask prices in real-time. This data is continuously processed to check against the user's buy/sell triggers.

3. Simulated Orders:
Instead of placing real buy/sell orders via Binance’s REST API, the app logs the prepared payloads when trigger prices are met. This is a simulation for educational purposes or strategy testing without risking real funds.

4. Console Output:
The console output is dynamically updated using ANSI escape codes to clear the screen and display the latest market data. This provides a clean and user-friendly display, simulating a dashboard interface.

5. Trigger-Based Logic:
The application monitors the real-time best bid and ask prices. When the market price reaches the user's predefined buy or sell trigger prices, the app prepares an order payload and logs it. If the price moves away from the trigger price, the prepared order is "canceled" and a new order can be prepared if the trigger condition is met again.

Libraries Used
Java-WebSocket (org.java-websocket): A Java library used to create WebSocket clients and servers. It handles the WebSocket connection, parsing messages, and sending requests. It is lightweight and fits well with applications requiring real-time data.

Maven dependency:

xml
Copy code
<dependency>
  <groupId>org.java-websocket</groupId>
  <artifactId>Java-WebSocket</artifactId>
  <version>1.5.2</version>
</dependency>
Gson (com.google.gson): Used for parsing and handling JSON data, which is the format used by Binance WebSocket for incoming market data.

Maven dependency:

xml
Copy code
<dependency>
  <groupId>com.google.code.gson</groupId>
  <artifactId>gson</artifactId>
  <version>2.8.8</version>
</dependency>

Future Enhancements
Implement real buy/sell order placement using Binance's REST API.
Add support for multiple trading pairs.
Incorporate more advanced order types (e.g., stop-limit, market).
Implement automated trading strategies based on technical indicators.
