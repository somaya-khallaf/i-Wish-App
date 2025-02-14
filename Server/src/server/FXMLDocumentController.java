/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import dal.DatabaseConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class FXMLDocumentController implements Initializable {

    Thread thread;
    ServerSocket myServerSocket, notificationServerSocket;
    boolean firstRun = false;
    @FXML
    private Button btStart;
    @FXML
    private Button btStop;

    public void handleStartAction(ActionEvent event) {
        LoggerUtil.info("Starting the server...");
        if (!firstRun) {
            try {
                myServerSocket = new ServerSocket(5005);
                notificationServerSocket = new ServerSocket(5000);
                DatabaseConnection.establishConnection();
                thread.start();
                firstRun = true;
            } catch (IOException ex) {
                LoggerUtil.error("The server failed to start:" + ex.getMessage());
            }
        } else {
            thread.resume();
        }
        LoggerUtil.info("The server has started successfully.");

    }

    public void handleStopAction(ActionEvent event) {
        thread.suspend();
        LoggerUtil.info("The server has stopped successfully.");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    DatabaseConnection.close();
                } catch (SQLException ex) {
                    LoggerUtil.error("Database connection closure failed: " + ex.getMessage());
                }
                while (true) {
                    try {
                        Socket socket = myServerSocket.accept();
                        Socket notificationSocket = notificationServerSocket.accept();
                        new ClientHandler(socket, notificationSocket);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        thread.setDaemon(true);
    }
}
