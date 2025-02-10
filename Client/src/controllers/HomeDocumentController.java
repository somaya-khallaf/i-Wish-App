package controllers;

import client.ServerConnection;
import client.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.HomePageDTO;
import dto.HomeUserDTO;
import dto.NotificationDTO;
import dto.ProductDTO;
import dto.WishDTO;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
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
    private VBox notificationVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label pointsLabel;
    @FXML
    private Button friendsButton;
    @FXML
    private Button addFriendButton;
    @FXML
    private Button requestsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button deleteBt;
    @FXML
    private Button insertBt;
    @FXML
    private HBox wishContainer;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Label itemLabel;
    @FXML
    private Label status;
    @FXML
    private ListView<Label> notificationListView;
    @FXML
    private Label notoficationLabel;
    ArrayList<WishDTO> wishList;
    ArrayList<NotificationDTO> notificationList;
    HomeUserDTO homeUserDTO;
    Thread thread;

    public HomeDocumentController(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    private void makeWishList() {
        for (WishDTO wish : wishList) {
            addWish(wish.getProductId(), wish.getProductName(), wish.getStatus());
        }
    }

    private void makeNotificationList() {
        for (NotificationDTO notification : notificationList) {
            notificationListView.getItems().add(new Label("⭐" + notification.getNotificationContent() + notification.getNotificationDate()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        JsonObject jsonResponse = serverConnection.sendRequest("getHomePage", null);
        HomePageDTO homePage = gson.fromJson(jsonResponse, HomePageDTO.class);
        homeUserDTO = homePage.getHomeUserDTO();
        wishList = homePage.getWishList();
        notificationList = homePage.getNotificationList();
        makeWishList();
        makeNotificationList();
        handleNotifications();
        thread.start();
        usernameLabel.setText(homeUserDTO.getUsername());
        pointsLabel.setText(String.valueOf(homeUserDTO.getBalance()));

    }

    private void handleNotifications() {
        thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    JsonObject jsonResponse = serverConnection.getNotifications();
                    NotificationDTO notification = gson.fromJson(jsonResponse, NotificationDTO.class);
                    notificationListView.getItems().add(0, new Label("⭐" + notification.getNotificationContent() + notification.getNotificationDate()));
                }
            }
        });
        thread.setDaemon(true);
    }

    @FXML
    private void handleFriendsButton(ActionEvent e) throws IOException {
        thread.stop();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendWindowDocument.fxml"));
        FriendWindowController fxmlDocumentController = new FriendWindowController(serverConnection,homeUserDTO.getBalance());
        loader.setController(fxmlDocumentController);
        Utils.moveToAntherScene(e, loader);
    }

    @FXML
    public void handleRequestsButton(ActionEvent e) throws IOException {
        thread.stop();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendRequestDocument.fxml"));
        FriendRequestController fxmlDocumentController = new FriendRequestController(serverConnection);
        loader.setController(fxmlDocumentController);
        Utils.moveToAntherScene(e, loader);
    }

    @FXML
    public void handleInsertButton(ActionEvent e) throws IOException {
        thread.stop();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MarketDocument.fxml"));
        //MarketController fxmlDocumentController = new MarketController(serverConnection);
        //loader.setController(fxmlDocumentController);
        Utils.moveToAntherScene(e, loader);
    }

    public void handleDeleteButton(ActionEvent e) throws IOException {
        ArrayList<Integer> products = new ArrayList<>();
        wishList.clear();
        for (Node node : WishListVBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                CheckBox checkBox = (CheckBox) hbox.lookup("#checkBox");
                Label itemLabel = (Label) hbox.lookup("#itemLabel");
                Label statusLabel = (Label) hbox.lookup("#status");
                if (checkBox != null && checkBox.isSelected()) {
                    products.add((int) hbox.getUserData());
                } else {
                    wishList.add(new WishDTO((int) hbox.getUserData(), itemLabel.getText(), statusLabel.getText()));
                }
            }
        }
        WishListVBox.getChildren().clear();
        makeWishList();
        JsonObject jsonResponse = serverConnection.sendRequest("removeWish", products);

    }

    @FXML
    public void handleAddFriendButton(ActionEvent e) throws IOException {
        thread.stop();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddFriendDocument.fxml"));
        AddFriendDocumentController fxmlDocumentController = new AddFriendDocumentController(serverConnection);
        loader.setController(fxmlDocumentController);
        Utils.moveToAntherScene(e, loader);
    }

    private void addWish(int productId, String productName, String status) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/wishItem.fxml"));
            HBox wishItem = loader.load();

            CheckBox checkBox = (CheckBox) wishItem.lookup("#checkBox");
            Label itemLabel = (Label) wishItem.lookup("#itemLabel");
            Label statusLabel = (Label) wishItem.lookup("#status");
            wishItem.setUserData(productId);
            itemLabel.setText(productName);
            statusLabel.setText(status);

            itemLabel.setPrefWidth(150);

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
        thread.stop();
        JsonObject jsonResponse = serverConnection.sendRequest("logout", null);
        serverConnection.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginDocument.fxml"));
        Utils.moveToAntherScene(e, loader);

    }
    @FXML
    private void handleRechargeButton(ActionEvent e) throws IOException {
        thread.stop();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RechargeDocument.fxml"));
        RechargeDocumentController fxmlDocumentController = new RechargeDocumentController(serverConnection);
        loader.setController(fxmlDocumentController);
        Utils.moveToAntherScene(e, loader);

    }
}
