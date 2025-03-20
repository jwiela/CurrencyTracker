package org.example.java_projekt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start_window.fxml"));
        Scene scene = new Scene(loader.load(), 444, 276);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("NBPapp");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}