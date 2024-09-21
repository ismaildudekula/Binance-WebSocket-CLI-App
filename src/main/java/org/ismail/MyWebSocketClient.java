package org.ismail;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.UUID;

public class MyWebSocketClient {

    private static double buyTriggerPrice;
    private static double sellTriggerPrice;
    private static String currentBuyOrderId;
    private static String currentSellOrderId;
    private static double preparedBuyPrice;
    private static double preparedSellPrice;

    public static void main(String[] args) {
        String binanceWebSocketUrl = "wss://stream.binance.com:9443/ws";

        // Get trigger prices from user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Buy Trigger Price: ");
        buyTriggerPrice = scanner.nextDouble();
        System.out.print("Enter Sell Trigger Price: ");
        sellTriggerPrice = scanner.nextDouble();
        scanner.close();

        try {
            WebSocketClient client = new WebSocketClient(new URI(binanceWebSocketUrl)) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    log("INFO", "Connected to Binance WebSocket.");
                    // Subscribe to the depth stream for BTC/USDT
                    subscribeToMarketData(this, "btcusdt@depth");
                }

                @Override
                public void onMessage(String message) {
                    try {
                        // Parse the incoming message
                        JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
                        if (jsonMessage.has("b") && jsonMessage.has("a")) { // Check for bids and asks
                            // Extract the best bid and ask
                            JsonArray bids = jsonMessage.getAsJsonArray("b");
                            JsonArray asks = jsonMessage.getAsJsonArray("a");

                            if (bids.size() > 0 && asks.size() > 0) {
                                double bestBidPrice = Double.parseDouble(bids.get(0).getAsJsonArray().get(0).getAsString());
                                double bestAskPrice = Double.parseDouble(asks.get(0).getAsJsonArray().get(0).getAsString());

                                // Update the CLI dynamically with new data on the same lines
                                updateCLI(bestBidPrice, bestAskPrice);

                                // Check for buy/sell trigger conditions
                                checkTriggers(bestBidPrice, bestAskPrice);
                            }
                        }
                    } catch (Exception e) {
                        log("ERROR", "Error while processing WebSocket message: " + e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log("INFO", "Connection closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    log("ERROR", "WebSocket encountered an error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            };

            client.connect();

        } catch (URISyntaxException e) {
            log("ERROR", "Invalid WebSocket URI: " + e.getMessage());
        }
    }

    // Method to send subscription message for selected streams
    public static void subscribeToMarketData(WebSocketClient client, String... pairs) {
        try {
            JsonObject subscriptionMessage = new JsonObject();
            subscriptionMessage.addProperty("method", "SUBSCRIBE");

            // Create a params array with the desired streams
            JsonArray params = new JsonArray();
            for (String pair : pairs) {
                params.add(pair);
            }

            subscriptionMessage.add("params", params);
            subscriptionMessage.addProperty("id", 1);  // You can use any ID

            // Send the subscription message
            client.send(subscriptionMessage.toString());
            log("INFO", "Subscription message sent: " + subscriptionMessage);
        } catch (Exception e) {
            log("ERROR", "Error while sending subscription message: " + e.getMessage());
        }
    }

    // Check if market prices hit buy/sell triggers based on best bid and ask prices
    private static void checkTriggers(double bestBidPrice, double bestAskPrice) {
        // Check if best ask price is <= buy trigger price
        if (bestAskPrice <= buyTriggerPrice && currentBuyOrderId == null) {
            log("INFO", "Buy trigger hit! Preparing BUY order payload...");
            currentBuyOrderId = prepareBuyOrderPayload(bestBidPrice);
            preparedBuyPrice = bestAskPrice;  // Store the prepared buy price
        } else if (currentBuyOrderId != null && bestAskPrice < preparedBuyPrice) {
            // If the price has moved away from the prepared buy price, cancel the order
            log("INFO", "Best ask price is lower than prepared buy price. Cancelling previous buy order...");
            cancelOrder(currentBuyOrderId);
            currentBuyOrderId = null;
        }

        // Check if best bid price is >= sell trigger price
        if (bestBidPrice >= sellTriggerPrice && currentSellOrderId == null) {
            log("INFO", "Sell trigger hit! Preparing SELL order payload...");
            currentSellOrderId = prepareSellOrderPayload(bestAskPrice);
            preparedSellPrice = bestBidPrice;  // Store the prepared sell price
        } else if (currentSellOrderId != null && bestBidPrice > preparedSellPrice) {
            // If the price has moved away from the prepared sell price, cancel the order
            log("INFO", "Best bid price is higher than prepared sell price. Cancelling previous sell order...");
            cancelOrder(currentSellOrderId);
            currentSellOrderId = null;
        }
    }

    // Prepare a simulated buy order payload
    private static String prepareBuyOrderPayload(double price) {
        try {
            JsonObject buyOrder = new JsonObject();
            buyOrder.addProperty("symbol", "BTCUSDT");
            buyOrder.addProperty("side", "BUY");
            buyOrder.addProperty("type", "LIMIT");
            buyOrder.addProperty("price", String.valueOf(price));
            buyOrder.addProperty("quantity", "0.001");
            buyOrder.addProperty("timeInForce", "GTC");

            // Generate a mock order ID
            String orderId = UUID.randomUUID().toString();
            buyOrder.addProperty("orderId", orderId);

            // Log the prepared buy order payload
            log("INFO", "Prepared Buy Order Payload: " + buyOrder.toString());
            return orderId;
        } catch (Exception e) {
            log("ERROR", "Error while preparing buy order payload: " + e.getMessage());
            return null;
        }
    }

    // Prepare a simulated sell order payload
    private static String prepareSellOrderPayload(double price) {
        try {
            JsonObject sellOrder = new JsonObject();
            sellOrder.addProperty("symbol", "BTCUSDT");
            sellOrder.addProperty("side", "SELL");
            sellOrder.addProperty("type", "LIMIT");
            sellOrder.addProperty("price", String.valueOf(price));
            sellOrder.addProperty("quantity", "0.001");
            sellOrder.addProperty("timeInForce", "GTC");

            // Generate a mock order ID
            String orderId = UUID.randomUUID().toString();
            sellOrder.addProperty("orderId", orderId);

            // Log the prepared sell order payload
            log("INFO", "Prepared Sell Order Payload: " + sellOrder.toString());
            return orderId;
        } catch (Exception e) {
            log("ERROR", "Error while preparing sell order payload: " + e.getMessage());
            return null;
        }
    }

    // Simulate order cancellation by preparing and displaying a cancellation payload
    private static void cancelOrder(String orderId) {
        try {
            JsonObject cancelOrderPayload = new JsonObject();
            cancelOrderPayload.addProperty("method", "order.cancel");
            cancelOrderPayload.addProperty("orderId", orderId);
            cancelOrderPayload.addProperty("symbol", "BTCUSDT");

            // Log the simulated cancellation payload
            log("INFO", "Cancellation Payload: " + cancelOrderPayload.toString());
        } catch (Exception e) {
            log("ERROR", "Error while preparing cancellation payload: " + e.getMessage());
        }
    }

    // Method to dynamically update CLI with bid and ask prices on the same line
    private static void updateCLI(double bestBidPrice, double bestAskPrice) {
        // ANSI code to clear the current line
        System.out.print("\033[2J"); // Clear screen
        System.out.print("\033[H");  // Move cursor to top left

        // Display updated bid and ask prices
        System.out.println("=================================");
        System.out.println("Real-Time Market Data for BTC/USDT");
        System.out.println("=================================");
        System.out.println("Buy Trigger Price: " + buyTriggerPrice);
        System.out.println("Sell Trigger Price: " + sellTriggerPrice);
        System.out.println("=================================");
        System.out.printf("Last Payload Buy Price: %.8f\n", preparedBuyPrice);
        System.out.printf("Last Payload Sell Price: %.8f\n", preparedSellPrice);
        System.out.println("=================================");
        System.out.printf("Best Ask Price: %.8f\n", bestAskPrice);
        System.out.printf("Best Bid Price: %.8f\n", bestBidPrice);
        System.out.println("=================================");
    }

    // Log method for consistent log messages
    private static void log(String level, String message) {
        System.out.println("[" + level + "] " + message);
    }
}
