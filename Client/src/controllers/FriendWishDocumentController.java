package controllers;

import client.LoadScenes;
import client.ServerConnection;
import client.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.WishDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class FriendWishDocumentController implements Initializable {

    @FXML
    private Label fName;
    @FXML
    private Label dobLabel;
    @FXML
    private Label totalPointsLabel;
    @FXML
    private TableView<WishDTO> friendWishTable;
    @FXML
    private TableColumn<WishDTO, String> wishColumn;
    @FXML
    private TableColumn<WishDTO, Double> fullpriceColumn;
    @FXML
    private TableColumn<WishDTO, Double> remainColumn;
    @FXML
    private TableColumn<WishDTO, String> statusColumn;
    @FXML
    private TableColumn<WishDTO, Void> contColumn;

    private ServerConnection serverConnection;
    private Gson gson = new Gson();
    private String friendUserName;
    private double totalBalance;

    public FriendWishDocumentController(ServerConnection serverConnection, String friendUserName, double totalBalance) {
        this.serverConnection = serverConnection;
        this.friendUserName = friendUserName;
        this.totalBalance = totalBalance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        wishColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        fullpriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        remainColumn.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        contColumn.setCellFactory(param -> new TableCell<WishDTO, Void>() {
            private final Button btn = new Button("Contribute");
            private final TextField textField = new TextField();
            private final Label completedLabel = new Label("Completed Wish");

            {
                btn.setOnAction((ActionEvent event) -> {
                    WishDTO wish = getTableView().getItems().get(getIndex());
                    String inputText = textField.getText();
                    try {
                        double contribution = Double.parseDouble(inputText);
                        handleContribution(wish, contribution);
                    } catch (NumberFormatException e) {
                        Utils.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");

                    }
                    textField.clear();
                });
                textField.setPromptText("Enter point");
                textField.setPrefWidth(100);
                //textField.setPrefHeight(8);
                btn.setPrefHeight(8);

                completedLabel.setPrefHeight(40);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setPrefHeight(45);
                if (empty) {
                    setGraphic(null);
                } else {
                    WishDTO wish = getTableView().getItems().get(getIndex());
                    if (wish != null && "Granted".equals(wish.getStatus())) {
                        setGraphic(completedLabel);
                    } else {
                        HBox hbox = new HBox(15, textField, btn);
                        setGraphic(hbox);
                    }
                }
            }
        });

        fetchFriendWishList();
    }

    private void fetchFriendWishList() {

        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("friendUserName", friendUserName);
        JsonObject jsonResponse = serverConnection.sendRequest("getFriendWishList", requestJson);

        String result = jsonResponse.get("Result").getAsString();
        if (result.equals("succeed")) {
            WishDTO[] wishesArray = gson.fromJson(jsonResponse.get("requests"), WishDTO[].class);
            ObservableList<WishDTO> wishes = FXCollections.observableArrayList(wishesArray);
            System.out.println(wishes.get(0).getWishId());
            friendWishTable.setItems(wishes);

            fName.setText(friendUserName);
            totalPointsLabel.setText(String.valueOf(totalBalance));
        } else {
            System.out.println("Failed to fetch friend's wish list.");
        }
    }

    private void handleContribution(WishDTO wish, double contribution) {

        if (contribution <= 0) {
            Utils.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Contribution must be greater than 0.");
            return;
        }
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("wishId", wish.getWishId());
        requestJson.addProperty("contribution", contribution);
        requestJson.addProperty("remaining", wish.getRemaining());
        requestJson.addProperty("friendUserName", friendUserName);
        requestJson.addProperty("productName", wish.getProductName());
        JsonObject jsonResponse = serverConnection.sendRequest("contributeToWish", requestJson);
        String result = jsonResponse.get("Result").getAsString();
        if (result.equals("succeed")) {
            fetchFriendWishList();
            totalPointsLabel.setText(String.valueOf(totalBalance - contribution));
        } else {
            System.out.println("Failed to contribute.");
            String message = jsonResponse.get("Message").getAsString();
            Utils.showAlert(Alert.AlertType.ERROR, "Error", message);
        }
    }

    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        LoadScenes.loadHomeScene();
    }
}
