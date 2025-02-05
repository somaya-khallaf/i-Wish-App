package controllers;


import client.ServerConnection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.HomePageDTO;
import dto.LoginDTO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;


public class HomeDocumentController implements Initializable {

    Gson gson = new Gson();
    JsonObject jsonObject;
    ServerConnection serverConnection;
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
    private Label delete;
    @FXML
    private Label insert;
    @FXML
    private HBox wishContainer;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Label itemLabel;
    @FXML
    private Label status;
    @FXML
    private ListView<?> notificationListView;
    @FXML
    private Label notoficationLabel;
    public HomeDocumentController(){}
    public HomeDocumentController(ServerConnection serverConnection) throws IOException{
        this.serverConnection = serverConnection;
        JsonObject jsonResponse = serverConnection.sendRequest("getHomePage", null);
        HomePageDTO homePage = gson.fromJson(jsonResponse, HomePageDTO.class);
        System.out.println(homePage);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
