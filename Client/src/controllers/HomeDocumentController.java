package controllers;

import client.LoadScenes;
import javafx.concurrent.Task;
import client.ServerConnection;
import client.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.HomePageDTO;
import dto.HomeUserDTO;
import dto.NotificationDTO;
import dto.WishDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class HomeDocumentController implements Initializable {

    Gson gson = new Gson();
    JsonObject jsonObject;
    ServerConnection serverConnection;
    @FXML
    private VBox WishListVBox;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label pointsLabel;
    @FXML
    private ListView<Label> notificationListView;
    @FXML
    ArrayList<WishDTO> wishList;
    ArrayList<NotificationDTO> notificationList;
    HomeUserDTO homeUserDTO;
    Thread thread;

    public HomeDocumentController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    private void makeWishList() {
        for (WishDTO wish : wishList) {
            addWish(wish.getWishId(), wish.getProductName(), wish.getStatus());
        }
    }

    private void makeNotificationList() {
        for (NotificationDTO notification : notificationList) {
            notificationListView.getItems().add(new Label("⭐" + notification.getNotificationContent() + " at " + notification.getNotificationDate()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("");
        JsonObject jsonResponse = serverConnection.sendRequest("getHomePage", null);
        HomePageDTO homePage = gson.fromJson(jsonResponse, HomePageDTO.class);
        homeUserDTO = homePage.getHomeUserDTO();
        wishList = homePage.getWishList();
        notificationList = homePage.getNotificationList();
        makeNotificationList();
        makeWishList();
        handleNotifications();

        usernameLabel.setText(homeUserDTO.getUsername());
        pointsLabel.setText(String.valueOf(homeUserDTO.getBalance()));

    }

    private void handleNotifications() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    System.out.println("ttt1");

                    JsonObject jsonResponse = serverConnection.getNotifications();
                    System.out.println("ttt2");
                    NotificationDTO notification = gson.fromJson(jsonResponse, NotificationDTO.class);
                    Platform.runLater(() -> {
                        notificationListView.getItems().add(0, new Label("⭐" + notification.getNotificationContent() + " at " + notification.getNotificationDate()));
                    });

                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Thread interrupted. Exiting loop.");
                        break;
                    }
                }
                return null;
            }
        };

        thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleFriendsButton(ActionEvent e) throws IOException {
        LoadScenes.loadFriendScene(homeUserDTO.getBalance());
    }

    @FXML
    public void handleRequestsButton(ActionEvent e) throws IOException {
        thread.interrupt();
        LoadScenes.loadFriendRequestScene();
    }

    @FXML
    public void handleInsertButton(ActionEvent e) throws IOException {
        thread.interrupt();
        LoadScenes.loadMarketScene();
    }

    public void handleDeleteButton(ActionEvent e) throws IOException {
        ArrayList<Integer> wishId = new ArrayList<>();
        wishList.clear();
        for (Node node : WishListVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                CheckBox checkBox = (CheckBox) hbox.lookup("#checkBox");
                Label itemLabel = (Label) hbox.lookup("#itemLabel");
                Label statusLabel = (Label) hbox.lookup("#status");
                if (checkBox != null && checkBox.isSelected()) {
                    wishId.add((int) hbox.getUserData());
                } else {
                    wishList.add(new WishDTO((int) hbox.getUserData(), itemLabel.getText(), statusLabel.getText()));
                }
            }
        }
        JsonObject jsonResponse = serverConnection.sendRequest("removeWish", wishId);
        String result = jsonResponse.get("Result").getAsString();
        if (result.equals("succeed")) {
            WishListVBox.getChildren().clear();
            makeWishList();
            Utils.showAlert(Alert.AlertType.INFORMATION, "Success", "Wish removed successfully.");
        } else {
            Utils.showAlert(Alert.AlertType.ERROR, "delete Failed", "Cannot delete a wish with contributions.");
        }

    }

    @FXML
    public void handleAddFriendButton(ActionEvent e) throws IOException {
        thread.interrupt();
        LoadScenes.loadAddFriendScene();
    }

    private void addWish(int wishId, String productName, String status) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WishItem.fxml"));
            HBox wishItem = loader.load();

            CheckBox checkBox = (CheckBox) wishItem.lookup("#checkBox");
            Label itemLabel = (Label) wishItem.lookup("#itemLabel");
            Label statusLabel = (Label) wishItem.lookup("#status");
            wishItem.setUserData(wishId);
            itemLabel.setText(productName);
            statusLabel.setText(status);

            itemLabel.setPrefWidth(150);

            itemLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(itemLabel, Priority.ALWAYS);

            statusLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(statusLabel, Priority.ALWAYS);
            if ("Pending".equals(status)) {
                statusLabel.setStyle("-fx-text-fill: red;");
            } else {
                statusLabel.setStyle("-fx-text-fill: green;");
            }

            WishListVBox.getChildren().add(wishItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlelogoutButton(ActionEvent e) throws IOException {
        thread.interrupt();
        JsonObject jsonResponse = serverConnection.sendRequest("logout", null);
        serverConnection.close();
        LoadScenes.loadLoginScene();
    }

    @FXML
    private void handleRechargeButton(ActionEvent e) throws IOException {
        thread.interrupt();
        LoadScenes.loadRechargeScene();

    }

    @FXML
    private void handleUpdateProfileButton(ActionEvent event) throws IOException {
        LoadScenes.loadUpdateScene();
    }
}
