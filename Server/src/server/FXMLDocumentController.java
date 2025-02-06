/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class FXMLDocumentController implements Initializable {

    Thread thread;
    ServerSocket myServerSocket, notificationServerSocket;
    boolean firstRun = false;
    @FXML
    private Button btStart;
    @FXML
    private Button btStop;

    public void handleStartAction(ActionEvent event) {
        if (!firstRun) {
            try {
                myServerSocket = new ServerSocket(5005);
                notificationServerSocket = new ServerSocket(5000);
                thread.start();
                firstRun = true;
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else
            thread.resume();
    }

    public void handleStopAction(ActionEvent event) {
        thread.suspend();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket socket = myServerSocket.accept();
                        Socket notificationSocket = notificationServerSocket.accept();
                        System.out.println(notificationSocket.getOutputStream());
                        new ClientHandler(socket, notificationSocket);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

    }
}
