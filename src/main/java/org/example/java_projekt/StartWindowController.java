package org.example.java_projekt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StartWindowController {
    @FXML private Button registerButton;
    @FXML private Button mainPanelButton;

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> openRegistrationWindow());
        mainPanelButton.setOnAction(event -> openMainPanelWindow());
    }

    private void openRegistrationWindow() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registration_window.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("User Registration");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMainPanelWindow() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main_panel.fxml"));
            Scene scene = new Scene(loader.load(), 640, 480);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("NBPapp");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
