/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.LoginDTO;
import static javafx.application.Application.launch;
    
/**
 *
 * @author helloss
 */
public class Client extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
    
}
/*
public class Client extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {

    }
    

    public static void main(String[] args) {
       
        try {
            Socket mySocket =  new Socket("127.0.0.1", 5005);
            PrintWriter writer = new PrintWriter(mySocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            Gson gson = new Gson();
            logInDTO loginData = new logInDTO("mohamed", "123");
            JsonObject jsonObject = gson.toJsonTree(loginData).getAsJsonObject();
            
            System.out.println(jsonObject);
            jsonObject.addProperty("command", "login");
            String jsonString = gson.toJson(jsonObject);
            writer.println(jsonString);
        
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
*/