package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection {

    private PrintWriter writer;
    private BufferedReader reader, notificationReader;
    private Socket mySocket, notificationSocket;
    private Gson gson;
    private static final int TIMEOUT_MILLISECONDS = 3000; // 10 seconds timeout

    public ServerConnection() {
        try {
            mySocket = new Socket("127.0.0.1", 5005);
            notificationSocket = new Socket("127.0.0.1", 5000);
            notificationReader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()));
            writer = new PrintWriter(mySocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            gson = new Gson();

            mySocket.setSoTimeout(TIMEOUT_MILLISECONDS);
            notificationSocket.setSoTimeout(TIMEOUT_MILLISECONDS);
        } catch (IOException ex) {
            Utils.showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the server. Please check if the server is running.");
            System.exit(0);
        }
    }

    public JsonObject sendRequest(String command, Object data) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("command", command);
            if (data != null) {
                jsonObject.add("data", gson.toJsonTree(data));
            }
            String jsonString = gson.toJson(jsonObject);
            writer.println(jsonString);
            String jsonResponse = reader.readLine();
            if (jsonResponse == null) {
                throw new IOException("Server closed the connection");
            }
            jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        } catch (SocketTimeoutException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Timeout", "The server is not responding. Please try again later.");
            System.exit(0);
            jsonObject.addProperty("Result", "failed");
            jsonObject.addProperty("Message", "Server timeout");
        } catch (IOException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Connection Error", "The server is not running or the connection was lost.");
            System.exit(0);
            jsonObject.addProperty("Result", "failed");
            jsonObject.addProperty("Message", "Connection error");
        }

        return jsonObject;
    }

    public JsonObject getNotifications() {
        JsonObject jsonObject = new JsonObject();
        try {
            String jsonResponse = notificationReader.readLine();
            if (jsonResponse != null) {
                jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            }
        } catch (IOException ex) {
            Utils.showAlert(Alert.AlertType.ERROR, "Notification Error", "Failed to read notifications from the server.");
        }
        return jsonObject;
    }

    public void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (notificationReader != null) notificationReader.close();
            if (notificationSocket != null) notificationSocket.close();
            if (mySocket != null) mySocket.close();
        } catch (IOException ex) {
            System.out.println("Error closing resources: " + ex.getMessage());
        }
    }
}