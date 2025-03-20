package org.example.java_projekt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class WeatherService {
    private String apiKey;

    public WeatherService() {
        try (InputStream input = new FileInputStream("config.txt")) {
            Properties prop = new Properties();
            prop.load(input);
            apiKey = prop.getProperty("weatherapi.key");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String fetchWeather() throws IOException {
        String urlString = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=Poznan";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String responseLine;
            StringBuilder response = new StringBuilder();
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine);
            }
            return parseWeatherResponse(response.toString());
        }
    }

    private String parseWeatherResponse(String response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.readValue(response, ObjectNode.class);

        String date = root.path("current").path("last_updated").asText();
        double temperatureDouble = root.path("current").path("temp_c").asDouble();
        int temperature = (int) temperatureDouble;
        String condition = root.path("current").path("condition").path("text").asText();

        return String.format("Date: %s, Temperature: %d°C, Condition: %s", date, temperature, condition);
    }
}
