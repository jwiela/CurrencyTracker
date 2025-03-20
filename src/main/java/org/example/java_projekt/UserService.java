package org.example.java_projekt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static Map<String, String> users = new HashMap<>();

    static {
        loadUsers();
    }

    public static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                users.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public static boolean validateLogin(String username, String password) {
        loadUsers();
        String hashedPassword = hashPassword(password);
        return users.containsKey(username) && users.get(username).equals(hashedPassword);
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
}