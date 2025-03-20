package org.example.java_projekt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class CurrencyService {
    public String fetchRate(String symbol, String date) throws IOException {
        String urlString = "http://api.nbp.pl/api/exchangerates/rates/A/" + symbol + "/" + date + "/?format=json";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine);
            }
            return parseRateResponse(response.toString());
        }
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String parseRateResponse(String response) throws IOException {

        JsonNode root = objectMapper.readTree(response);
        String date = root.path("rates").get(0).path("effectiveDate").asText();
        String currency = root.path("code").asText();
        String rate = root.path("rates").get(0).path("mid").asText();
        return String.format("%s, %s, %s PLN", date, currency, rate);
    }

    public void saveRate(String symbol, String rate) throws IOException {
        try (FileWriter writer = new FileWriter(symbol + ".txt", true)) {
            writer.write(rate + "\n");
        }
    }

    public List<String> loadRates(String symbol) throws IOException {
        List<String> rates = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(symbol + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rates.add(line);
            }
        }
        return rates;
    }
}