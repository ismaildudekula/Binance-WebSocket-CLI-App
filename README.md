# Binance WebSocket CLI Application

## Overview

This is a Java-based command-line application that connects to Binance's WebSocket API to fetch real-time market depth data for BTC/USDT. Users can set buy and sell trigger prices, simulate order placement when those prices are hit, and cancel the orders if the price moves away from the prepared order price. **No real orders are sent to Binance**, but the app logs the payloads for simulation purposes.

## Features

- Real-time BTC/USDT best bid and ask price updates.
- Simulates buy and sell order preparation when trigger prices are hit.
- Cancels prepared orders if the price moves away from the triggered price.
- Clean console output with continuous updates.
- Uses WebSocket for low-latency, real-time price updates.

## Installation

1. Clone the repository:
   ```bash
   https://github.com/ismaildudekula/Binance-WebSocket-CLI-App.git
   cd Binance-WebSocket_CLI-App
   ```

2. Ensure you have Java installed. You can check your version with:
   ```bash
   java -version
   ```

3. Install Maven if you don't already have it. You can verify your Maven installation with:
   ```bash
   mvn -version
   ```

4. Build the project using Maven:
   ```bash
   mvn clean install
   ```

## Running the Application

To run the application, execute the following command:
```bash
mvn exec:java -Dexec.mainClass="org.ismail.MyWebSocketClient"
```

The application will prompt you to enter:
- **Buy Trigger Price**
- **Sell Trigger Price**

Once entered, it will connect to Binance and start receiving real-time price updates. It will monitor the price and simulate buy/sell orders when the conditions are met.

### Sample Interaction:

```bash
Enter Buy Trigger Price: 63000.00
Enter Sell Trigger Price: 64000.00

[INFO] Connected to Binance WebSocket.
[INFO] Subscription message sent: {"method":"SUBSCRIBE","params":["btcusdt@depth"],"id":1}
=================================
Real-Time Market Data for BTC/USDT
=================================
Buy Trigger Price: 63000.00
Sell Trigger Price: 64000.00
=================================
Last Payload Buy Price: 62994.15000000
Last Payload Sell Price: 63065.45000000
=================================
Best Ask Price: 63010.00
Best Bid Price: 62950.00
=================================
[INFO] Buy trigger hit! Preparing BUY order payload...
[INFO] Prepared Buy Order Payload: {"symbol":"BTCUSDT","side":"BUY","type":"LIMIT","price":"63000.00","quantity":"0.001","timeInForce":"GTC","orderId":"uuid-1234"}
```
## 1. Start - Subscribe - BUY TRIGGER EXAMPLE
![1 start - buy trigger - edit](https://github.com/user-attachments/assets/8ffa7484-7972-4b58-8d3e-f142c619d66a)

## 2. Cancelling Buy Payload (if best ask becomes lower than previous payload) and Re-making Buy Payload (according to new price)
![2  cancel Buy - re buy - edit](https://github.com/user-attachments/assets/04ee2501-353e-4464-8760-96ace167e080)

## 3. Sell Trigger Example
![3  Trigger Sell - edit](https://github.com/user-attachments/assets/5d550af9-d288-4f2c-ab66-1023370aa826)

##  4. Cancelling Sell Payload (if best bid becomes higher than previous payload) and Re-making Sell Payload (according to new price)
![4  Cancel Sell - re create sell payload - edit](https://github.com/user-attachments/assets/46e8de82-6d63-44a8-b123-24b37e885b5d)

## Design Decisions

- **WebSocket Client:** Used `org.java_websocket.client.WebSocketClient` to establish a low-latency, persistent connection to Binance's WebSocket API.
- **Gson Library:** This is used to parse incoming JSON data from Binance and prepare JSON payloads for simulated orders.
- **Trigger Logic:** The buy trigger is hit when the **best ask price** is less than or equal to the buy trigger price, and the sell trigger is hit when the **best bid price** is greater than or equal to the sell trigger price. Prepared orders are canceled if the price moves outside the trigger conditions.

## Libraries Used

- [Java WebSocket](https://github.com/TooTallNate/Java-WebSocket): For WebSocket communication.
- [Gson](https://github.com/google/gson): For JSON parsing and manipulation.

## License

This project is licensed under the MIT License.
