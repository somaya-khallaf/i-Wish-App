/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

/**
 *
 * @author Ahmed
 */
import client.ServerConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class FriendWindowController implements Initializable {
    ServerConnection serverConnection;
    public FriendWindowController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
    public FriendWindowController() {
    }

    @FXML
    private void handleAddtButton(ActionEvent event) {
    }

    @FXML
    private void handleWishButton(ActionEvent event) {
    }

    @FXML
    private void handleRemoveButton(ActionEvent event) {
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
    }

    public void initialize(URL url, ResourceBundle rb) {
    }
}
