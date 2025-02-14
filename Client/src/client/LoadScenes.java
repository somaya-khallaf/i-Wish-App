/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import controllers.AddFriendDocumentController;
import controllers.FriendRequestController;
import controllers.FriendWindowController;
import controllers.FriendWishDocumentController;
import controllers.HomeDocumentController;
import controllers.LoginDocumentController;
import controllers.MarketDocumentController;
import controllers.RechargeDocumentController;
import controllers.SignupDocumentController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoadScenes {

    private static FXMLLoader loginSceneLoader;
    private static FXMLLoader signupSceneLoader;
    private static FXMLLoader homeSceneLoader;
    private static FXMLLoader friendSceneLoader;
    private static FXMLLoader friendRequestSceneLoader;
    private static FXMLLoader marketSceneLoader;
    private static FXMLLoader addFriendSceneLoader;
    private static FXMLLoader rechargeSceneLoader;
    private static FXMLLoader friendWishSceneLoader;

    private static SignupDocumentController signupController;
    private static LoginDocumentController loginController;
    private static HomeDocumentController homeController;
    private static FriendWindowController friendController;
    private static FriendRequestController friendRequestController;
    private static MarketDocumentController marketController;
    private static AddFriendDocumentController addFriendController;
    private static RechargeDocumentController rechargeController;
    private static FriendWishDocumentController friendWishController;

    private static Stage stage;
    private static LoadScenes loadScene;
    private static ServerConnection serverConnection;

    private LoadScenes(Stage stage) {
        this.stage = stage;
    }

    static private void loadControllers() {
        signupController = new SignupDocumentController(serverConnection);
        loginController = new LoginDocumentController(serverConnection);
        homeController = new HomeDocumentController(serverConnection);
        friendRequestController = new FriendRequestController(serverConnection);
        marketController = new MarketDocumentController(serverConnection);
        addFriendController = new AddFriendDocumentController(serverConnection);
        rechargeController = new RechargeDocumentController(serverConnection);
    }

    static public void loadLoginScene() throws IOException {
        serverConnection = new ServerConnection();
        loadControllers();
        loginSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/LoginDocument.fxml"));
        loginSceneLoader.setController(loginController);
        loadScene(loginSceneLoader);
    }

    static public void loadSignupScene() throws IOException {
        signupSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/SignupDocument.fxml"));
        signupSceneLoader.setController(signupController);
        loadScene(signupSceneLoader);
    }

    static public void loadHomeScene() throws IOException {
        homeSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/HomeDocument.fxml"));
        homeSceneLoader.setController(homeController);
        loadScene(homeSceneLoader);
    }

    static public void loadFriendScene(double balance) throws IOException {
        friendSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/FriendWindowDocument.fxml"));
        friendController = new FriendWindowController(serverConnection, balance);
        friendSceneLoader.setController(friendController);
        loadScene(friendSceneLoader);
    }

    static public void loadFriendRequestScene() throws IOException {
        friendRequestSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/FriendRequestDocument.fxml"));
        friendRequestSceneLoader.setController(friendRequestController);
        loadScene(friendRequestSceneLoader);
    }

    static public void loadMarketScene() throws IOException {
        marketSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/MarketDocument.fxml"));
        marketSceneLoader.setController(marketController);
        loadScene(marketSceneLoader);
    }

    static public void loadAddFriendScene() throws IOException {
        addFriendSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/AddFriendDocument.fxml"));
        addFriendSceneLoader.setController(addFriendController);
        loadScene(addFriendSceneLoader);
    }

    static public void loadRechargeScene() throws IOException {
        rechargeSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/RechargeDocument.fxml"));
        rechargeSceneLoader.setController(rechargeController);

        loadScene(rechargeSceneLoader);
    }

    static public void loadFriendWisheScene(String Friendusername, double totalPalance) throws IOException {
        friendWishSceneLoader = new FXMLLoader(LoadScenes.class.getResource("/fxml/FriendWishDocument.fxml"));
        friendWishController = new FriendWishDocumentController(serverConnection, Friendusername, totalPalance);
        friendWishSceneLoader.setController(friendWishController);
        loadScene(friendWishSceneLoader);
    }

    static private void loadScene(FXMLLoader loader) throws IOException {
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();

    }

    public static  LoadScenes getLoadSceneObj(Stage stage) {
        if (loadScene == null) {
            loadScene = new LoadScenes(stage);
        }
        return loadScene;
    }
    ;
}
