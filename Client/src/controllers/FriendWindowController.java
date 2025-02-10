package controllers;

import client.ServerConnection;
import client.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.FriendDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class FriendWindowController implements Initializable {

    Gson gson = new Gson();
    @FXML
    private ListView<FriendDTO> friendListView;
    private ServerConnection serverConnection;
    @FXML
    private Button backBtn;
    @FXML
    private Button addBtn;
    private double totalPalance;

    public FriendWindowController(ServerConnection serverConnection,double totalPalance) {
        this.serverConnection = serverConnection;
        this.totalPalance = totalPalance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        JsonObject jsonResponse = serverConnection.sendRequest("getFriendList", null);
        String result = jsonResponse.get("Result").getAsString();
        if (result.equals("succeed")) {
            FriendDTO[] friendsArray = gson.fromJson(jsonResponse.get("requests"), FriendDTO[].class);
            ArrayList<FriendDTO> friends = new ArrayList<>(Arrays.asList(friendsArray));
            setFriendList(friends);
        } else {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Friend List", "You have no friends.");
        }
    }

    public void setFriendList(ArrayList<FriendDTO> friends) {
        friendListView.getItems().setAll(friends);

        friendListView.setCellFactory(new Callback<ListView<FriendDTO>, ListCell<FriendDTO>>() {
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
                            Label usernameLabel = new Label("Full name:");
                            usernameLabel.setPrefWidth(100);
                            usernameLabel.setPrefHeight(50);

                            TextField usernameField = new TextField(item.getFriendfullname());
                            usernameField.setEditable(false);
                            usernameField.setPrefWidth(250);
                            usernameField.setPrefHeight(50);

                            Button wishBtn = new Button("Wish");
                            Button removeBtn = new Button("Remove");

                            wishBtn.setPrefWidth(130);
                            wishBtn.setPrefHeight(50);
                            removeBtn.setPrefWidth(130);
                            removeBtn.setPrefHeight(50);

                            wishBtn.setOnAction(event -> handleWish(event, item));
                            removeBtn.setOnAction(event -> handleRemove(item));

                            hbox.getChildren().addAll(usernameLabel, usernameField, wishBtn, removeBtn);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void handleWish(ActionEvent event, FriendDTO friend) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendWishDocument.fxml"));
            FriendWishDocumentController homeController = new FriendWishDocumentController(serverConnection, friend.getFriendusername(),totalPalance);          
            loader.setController(homeController);
            Utils.moveToAntherScene(event, loader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRemove(FriendDTO friend) {
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("Friendusername", friend.getFriendusername());
        JsonObject jsonResponse = serverConnection.sendRequest("removeFriend", requestJson);
        String result = jsonResponse.get("Result").getAsString();
        if (result.equals("succeed")) {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Friend List", "Friend removed successfully.");
            if (friendListView != null && friend != null) {
                friendListView.getItems().remove(friend);
            } else {
                System.out.println("Error: One of the objects is null.");
            }
        }
    }

    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeDocument.fxml"));
        HomeDocumentController homeController = new HomeDocumentController(serverConnection);
        loader.setController(homeController);
        Utils.moveToAntherScene(event, loader);
    }

    @FXML
    private void handleAddAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddFriendDocument.fxml"));
        AddFriendDocumentController homeController = new AddFriendDocumentController(serverConnection);
        loader.setController(homeController);
        Utils.moveToAntherScene(event, loader);
    }
}
