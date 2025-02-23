/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import client.LoadScenes;
import client.ServerConnection;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class AddFriendDocumentController implements Initializable {

    Gson gson = new Gson();
    @FXML
    private TableView<FriendDTO> addTable;
    @FXML
    private TableColumn<FriendDTO, String> userNameColumn;
    @FXML
    private TableColumn<FriendDTO, Void> addColumn;
    @FXML
    private TableColumn<FriendDTO, String> fullNameColumn;
    @FXML
    private TextField searchField;
    ServerConnection serverConnection;
    private ObservableList<FriendDTO> userList = FXCollections.observableArrayList();

    public AddFriendDocumentController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @FXML
    private void handleBackAction(ActionEvent event) {
        try {
            LoadScenes.loadHomeScene();
        } catch (IOException ex) {
            Logger.getLogger(AddFriendDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleSearchButton(ActionEvent event) {
            JsonObject jsonResponse = serverConnection.sendRequest("getFriend", searchField.getText());
            FriendDTO[] requestsArray = gson.fromJson(jsonResponse.get("requests"), FriendDTO[].class);
            userList.clear();
            String result = jsonResponse.get("Result").getAsString();
            if (result.equals("succeed")) {
                ArrayList<FriendDTO> suggestions = new ArrayList<>(Arrays.asList(requestsArray));
                loadUsers(suggestions);
            }
            addTable.setItems(userList);

        } 

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("Friendusername"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("Friendfullname"));
        addColumn.setCellFactory(column -> new AddButtonCell());

    }

    private void loadUsers(ArrayList<FriendDTO> suggestions) {
        for (FriendDTO suggestion : suggestions) {
            userList.add(suggestion);
        }

    }

    private class AddButtonCell extends TableCell<FriendDTO, Void> { // Corrected generic type

        private final Button addButton = new Button("Add");

        public AddButtonCell() {
            userNameColumn.getStyleClass().add("username-column");
            fullNameColumn.getStyleClass().add("fullname-column");
            addButton.getStyleClass().add("add-button");
            addButton.setOnAction(event -> {
                FriendDTO user = getTableView().getItems().get(getIndex());
                JsonObject jsonResponse = serverConnection.sendRequest("addFriend", user.getFriendusername());
                System.out.println("Adding friend: " + user.getFriendusername());
                getTableView().getItems().remove(getIndex());
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(new HBox(addButton));
            }
        }
    }
}
