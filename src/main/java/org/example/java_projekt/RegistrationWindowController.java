package org.example.java_projekt;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RegistrationWindowController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerButton;
    @FXML private Label errorLabel_reg;

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> registerUser());
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel_reg.setText("Username and password fields cannot be empty.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.split(":")[0].equals(username)) {
                    errorLabel_reg.setText("Username already exists.");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String hashedPassword = hashPassword(password);

        try (FileWriter writer = new FileWriter("users.txt", true)) {
            writer.write(username + ":" + hashedPassword + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.close();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
