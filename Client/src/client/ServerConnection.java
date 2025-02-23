package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerConnection {

    private PrintWriter writer;
    private BufferedReader reader, notificationReader;
    private Socket mySocket, notificationSocket;
    private Gson gson;
    private static final int TIMEOUT_MILLISECONDS = 10000;
    String myToken = "";

    private void establishConnection() {
        try {
            mySocket = new Socket("127.0.0.1", 5005);
            writer = new PrintWriter(mySocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            gson = new Gson();
            mySocket.setSoTimeout(TIMEOUT_MILLISECONDS);
        } catch (IOException ex) {
            Utils.showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the server. Please check if the server is running.");
            System.exit(0);
        }
    }

    public void extablishNotificaionConnection() {
        try {
            notificationSocket = new Socket("127.0.0.1", 5000);
            notificationReader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()));
        } catch (IOException ex) {
            Utils.showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the server. Please check if the server is running.");
            System.exit(0);
        }
    }

    public JsonObject sendRequest(String command, Object data) {
        establishConnection();
        JsonObject jsonObject = new JsonObject();
        try {

            jsonObject.addProperty("command", command);
            jsonObject.addProperty("token", myToken);
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
            if (jsonObject.has("token")) {
                myToken = jsonObject.get("token").getAsString();
                jsonObject.remove("token");
            }

        } catch (SocketTimeoutException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Timeout", "The server is not responding. Please try again later.");
            System.exit(0);
        } catch (IOException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Connection Error", "The server is not running or the connection was lost.");
            System.exit(0);

        }
        close();
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
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (mySocket != null) {
                mySocket.close();
            }
        } catch (IOException ex) {
            System.out.println("Error closing resources: " + ex.getMessage());
        }
    }
}
