package controllers;

import client.ServerConnection;
import client.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.FriendDTO;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

public class FriendRequestController implements Initializable {

    Gson gson = new Gson();
    @FXML
    private ListView<FriendDTO> friendRequestList; // Use FriendDTO
    ServerConnection serverConnection;
    @FXML
    private Button backBtn;

    FriendRequestController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            JsonObject jsonResponse = serverConnection.sendRequest("getFriendRequestList", null);
            System.out.println("Server response: " + jsonResponse);
            String result = jsonResponse.get("Result").getAsString();
            if (result.equals("succeed")) {
                FriendDTO[] requestsArray = gson.fromJson(jsonResponse.get("requests"), FriendDTO[].class);
                ArrayList<FriendDTO> requests = new ArrayList<>(Arrays.asList(requestsArray));
                setFriendRequestList(requests);
            } else {
                Utils.showAlert(Alert.AlertType.INFORMATION, "Friend Requests", "You have no pending friend requests.");
            }
        } catch (IOException ex) {
            Logger.getLogger(FriendRequestController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // This method sets the friend requests to the ListView
    public void setFriendRequestList(ArrayList<FriendDTO> requests) {
        friendRequestList.getItems().setAll(requests); // Add all friend requests to the ListView

        // Set a custom cell factory to display friend requests with HBox
        friendRequestList.setCellFactory(new Callback<ListView<FriendDTO>, ListCell<FriendDTO>>() {
            @Override
            public ListCell<FriendDTO> call(ListView<FriendDTO> param) {
                return new ListCell<FriendDTO>() {
                    @Override
                    protected void updateItem(FriendDTO item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10);

                            Label usernameLabel = new Label("Username:");
                            usernameLabel.setPrefWidth(100); // Set a specific width for username label
                            usernameLabel.setPrefHeight(50);

                            TextField usernameField = new TextField(item.getFriendusername());
                            usernameField.setEditable(false);
                            usernameField.setPrefWidth(150); // Set a specific width for username text field
                            usernameField.setPrefHeight(50);

                            Label fullnameLabel = new Label("Full Name:");
                            fullnameLabel.setPrefWidth(100); // Set a specific width for fullname label
                            fullnameLabel.setPrefHeight(50);

                            TextField fullnameField = new TextField(item.getFriendfullname());
                            fullnameField.setEditable(false);
                            fullnameField.setPrefWidth(200); // Set a specific width for fullname text field
                            fullnameField.setPrefHeight(50);

                            Button acceptBtn = new Button("Accept");
                            Button rejectBtn = new Button("Reject");
                            acceptBtn.setPrefWidth(100);
                            acceptBtn.setPrefHeight(50);
                            rejectBtn.setPrefWidth(100);
                            rejectBtn.setPrefHeight(50);

                            acceptBtn.setOnAction(event -> handleAccept(item));
                            rejectBtn.setOnAction(event -> handleReject(item));

                            // Add all elements into the HBox
                            hbox.getChildren().addAll(usernameLabel, usernameField, fullnameLabel, fullnameField, acceptBtn, rejectBtn);
                            setGraphic(hbox);
                        }
                    }

                    private void handleReject(FriendDTO item) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };
            }
        });
    }

    @FXML
    private void handleBackAction(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeDocument.fxml"));
            HomeDocumentController fxmlDocumentController = new HomeDocumentController(serverConnection);
            loader.setController(fxmlDocumentController);
            Utils.moveToAntherScene(event, loader);
        } catch (IOException ex) {
            Logger.getLogger(FriendRequestController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleAccept(FriendDTO request) {
        System.out.println("Accepted: " + request.getFriendusername());
    }

    // Handle Reject button click
    private void handleReject(FriendDTO request) {
        System.out.println("Rejected: " + request.getFriendusername());
    }
}
