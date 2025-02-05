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
    BufferedReader reader;
    Socket mySocket;
    JsonObject jsonObject;
    Gson gson;

    public ServerConnection() {
        try {
            mySocket = new Socket("127.0.0.1", 5005);
            writer = new PrintWriter(mySocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            gson = new Gson();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public JsonObject sendRequest(String command, Object data) throws IOException{
        jsonObject = new JsonObject();
        if (data!= null)
            jsonObject = gson.toJsonTree(data).getAsJsonObject();
        jsonObject.addProperty("command", command);
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
        String jsonResponse = reader.readLine();
        jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        return jsonObject;
    }
}
