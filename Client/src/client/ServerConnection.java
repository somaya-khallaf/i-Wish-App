/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerConnection {

    PrintWriter writer;
    BufferedReader reader, notificationReader;
    Socket mySocket, notificationSocket;
    JsonObject jsonObject;
    Gson gson;

    public ServerConnection() {
        try {
            mySocket = new Socket("127.0.0.1", 5005);
            notificationSocket = new Socket("127.0.0.1", 5000);
            notificationReader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()));
            writer = new PrintWriter(mySocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            gson = new Gson();
        } catch (IOException ex) {
            try {
                reader.close();
                writer.close();
                mySocket.close();
                notificationSocket.close();
            } catch (IOException ex1) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public JsonObject sendRequest(String command, Object data) {
        try {
            jsonObject = new JsonObject();
            if (data != null) {
                jsonObject.add("data", gson.toJsonTree(data));
            }
            jsonObject.addProperty("command", command);
            String jsonString = gson.toJson(jsonObject);
            writer.println(jsonString);
            String jsonResponse = reader.readLine();
            jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        } catch (IOException ex) {
            try {
                reader.close();
                writer.close();
                mySocket.close();
                notificationSocket.close();
            } catch (IOException ex1) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return jsonObject;

    }

    public JsonObject getNotifications() {
        jsonObject = new JsonObject();
        try {
            String jsonResponse = notificationReader.readLine();
            jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        } catch (IOException ex) {
            try {
                notificationReader.close();
                notificationSocket.close();
            } catch (IOException ex1) {
                Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        return jsonObject;
    }

    public void close() {
        try {
            reader.close();
            writer.close();
            notificationReader.close();
            notificationSocket.close();
            mySocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
