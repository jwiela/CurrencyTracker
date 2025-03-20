package org.example.java_projekt;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.image.ImageView;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainPanelController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private VBox mainPanel;
    @FXML private TextField currencySymbolField;
    @FXML private Button fetchRateButton;
    @FXML private Button viewRatesButton;
    @FXML private Label errorLabel;
    @FXML private DatePicker datePicker;
    @FXML private Label loginLabel;
    @FXML private ImageView weatherImageView;
    @FXML private ImageView NBPimageView;

    private Timer weatherUpdateTimer;
    private WeatherService weatherService = new WeatherService();
    private CurrencyService currencyService = new CurrencyService();

    @FXML
    private void initialize() {
        mainPanel.setVisible(false);
        loginButton.setOnAction(event -> loginUser());
        fetchRateButton.setOnAction(event -> fetchCurrencyRate());
        viewRatesButton.setOnAction(event -> viewCurrencyRates());

        startWeatherUpdates();
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (UserService.validateLogin(username, password)) {
            mainPanel.setVisible(true);
            usernameField.setVisible(false);
            passwordField.setVisible(false);
            loginButton.setVisible(false);
            loginLabel.setVisible(false);
            errorLabel.setText("");
        } else {
            errorLabel.setText("Invalid username or password");
        }

        Image NBP = new Image(getClass().getResource("/images/NBP.png").toExternalForm());
        NBPimageView.setImage(NBP);
        NBPimageView.setFitHeight(100);
        NBPimageView.setFitWidth(100);
    }

    private void fetchCurrencyRate() {
        String symbol = currencySymbolField.getText().toUpperCase();
        LocalDate selectedDate = datePicker.getValue();

        if(selectedDate == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a date");
            alert.show();
            return;
        }

        String formattedDate = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        try {
            String rate = currencyService.fetchRate(symbol, formattedDate);
            if (rate != null) {
                currencyService.saveRate(symbol, rate);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Rate fetched and saved: " + rate);
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to fetch rate for " + symbol);
                alert.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error fetching rate: " + e.getMessage());
            alert.show();
        }
    }

    private void viewCurrencyRates() {
        String symbol = currencySymbolField.getText().toUpperCase();
        try {
            List<String> rates = currencyService.loadRates(symbol);
            if (rates.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No rates found for " + symbol);
                alert.show();
            } else {
                Stage stage = new Stage();
                TableView<String[]> table = new TableView<>();

                String[] columnNames = {"Date", "Currency", "Rate"};
                for (int i = 0; i < columnNames.length; i++) {
                    final int finalIdx = i;
                    TableColumn<String[], String> column = new TableColumn<>(columnNames[i]);
                    column.setCellValueFactory(param -> new SimpleStringProperty((param.getValue()[finalIdx])));
                    table.getColumns().add(column);
                }

                // Create a line chart
                final NumberAxis xAxis = new NumberAxis();
                final NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("Date");
                yAxis.setLabel("Rate");
                final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Currency Rates Over Time");

                // Create a series and add data to it
                XYChart.Series series = new XYChart.Series();
                series.setName(symbol + " Rates");

                int dateIndex = 0;
                for (String rate : rates) {
                    String[] rateParts = rate.split(", ");
                    table.getItems().add(rateParts);

                    // Remove non-numeric characters from the rate string
                    String rateValue = rateParts[2].replace(" PLN", "");

                    // Add data to the series
                    series.getData().add(new XYChart.Data(dateIndex++, Double.parseDouble(rateValue)));
                }

                // Add the series to the line chart
                lineChart.getData().add(series);

                VBox root = new VBox(table, lineChart);
                stage.setScene(new Scene(root));
                stage.setTitle(symbol + " Rates");
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading rates: " + e.getMessage());
            alert.show();
        }
    }

    private void startWeatherUpdates() {
        weatherUpdateTimer = new Timer(true);
        weatherUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateWeather());
            }
        }, 0, 300000);
    }

    @FXML private VBox weatherInfoBox;

    private void updateWeather() {
        try {
            String weather = weatherService.fetchWeather();
            String[] weatherLines = weather.split(", ");

            weatherInfoBox.getChildren().clear();
            for (String line : weatherLines) {
                Label weatherLineLabel = new Label(line);
                weatherLineLabel.setTextFill(Color.WHITE);
                weatherInfoBox.getChildren().add(weatherLineLabel);
            }

            Image weatherImage;
            if(weather.toLowerCase().contains("sunny")){
                weatherImage = new Image(getClass().getResource("/images/sunny.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            } else if(weather.toLowerCase().contains("clear")){
                weatherImage = new Image(getClass().getResource("/images/clear_night.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
            else if(weather.toLowerCase().contains("partly cloudy")){
                weatherImage = new Image(getClass().getResource("/images/partlycloudy.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
            else if(weather.toLowerCase().contains("cloudy")){
                weatherImage = new Image(getClass().getResource("/images/cloudy.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
            else if(weather.toLowerCase().contains("thunder")){
                weatherImage = new Image(getClass().getResource("/images/thunder.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
            else if(weather.toLowerCase().contains("snow")){
                weatherImage = new Image(getClass().getResource("/images/snow.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
            else if(weather.toLowerCase().contains("rain")){
                weatherImage = new Image(getClass().getResource("/images/rain.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
            else if(weather.toLowerCase().contains("mist")){
                weatherImage = new Image(getClass().getResource("/images/mist.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
            else if(weather.toLowerCase().contains("overcast")){
                weatherImage = new Image(getClass().getResource("/images/overcast.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
            else {
                weatherImage = new Image(getClass().getResource("/images/basic.png").toExternalForm());
                weatherImageView.setImage(weatherImage);
                weatherImageView.setFitHeight(50);
                weatherImageView.setFitWidth(50);
                weatherInfoBox.getChildren().add(weatherImageView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error fetching weather: " + e.getMessage());
            errorLabel.setTextFill(Color.WHITE);
            weatherInfoBox.getChildren().clear();
            weatherInfoBox.getChildren().add(errorLabel);
        }
    }
}
