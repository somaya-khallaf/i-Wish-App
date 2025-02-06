package server;

import dto.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.ServerSocket;
import java.sql.SQLException;

import entities.User;
import sl.*;

public class ClientHandler extends Thread {

    private String userName;
    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private Gson gson = new Gson();
    private JsonObject jsonObject;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            start();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (true) {
            try {
                String jsonString = reader.readLine();
                jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                handleCommand(jsonObject);
            } catch (IOException ex) {
                try {
                    reader.close();
                    writer.close();
                    socket.close();
                } catch (IOException ex1) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    public void handleCommand(JsonObject jsonObject) {
        String command = jsonObject.get("command").getAsString();
        if (command.equals("login")) {
            handleLogIn(jsonObject);
        } else if (command.equals("getHomePage")) {
            handleHomePage();
        } else if (command.equals("register")) {
            handleRegister(jsonObject);
        } else if (command.equals("getProductlist")) {
            handleProductList();
        } else if (command.equals("addWish")) {
            handleAddingWish(jsonObject);
        } else if (command.equals("removeWish")) {
            handleRemovingWish(jsonObject);
        } else if (command.equals("getFriendRequestList")) {
            handleFriendRequestList();
        } else if (command.equals("acceptFriendRequest")) {
            handleAcceptingFriendRequest(jsonObject);
        } else if (command.equals("rejectFriendRequest")) {
            handleRejectingFriendRequest(jsonObject);
        } else if (command.equals("getFriendList")) {
            handleFriendList(jsonObject);
        } else if (command.equals("getFriendWishList")) {
            handleFriendWishList(jsonObject);
        } else if (command.equals("removeFriend")) {
            handleRemovingFriend(jsonObject);
        } else if (command.equals("addFriend")) {
            handleAddingFriend();
        }

    }

    private void handleHomePage() {
        try {
            HomeUserDTO userData = UserSL.getUserData(userName);
            ArrayList<WishDTO> wishList = WishSL.getWishList(userName);
            ArrayList<NotificationDTO> NotificationList = NotificationSL.getNotificationList(userName);
            HomePageDTO homePage = new HomePageDTO(userData, wishList, NotificationList);
            jsonObject = gson.toJsonTree(homePage).getAsJsonObject();
            String jsonString = gson.toJson(jsonObject);
            writer.println(jsonString);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleLogIn(JsonObject jsonObject) {
        LoginDTO loginData = gson.fromJson(jsonObject, LoginDTO.class);

        System.out.println(loginData);
        jsonObject = new JsonObject();
        try {
            if (UserSL.logIn(loginData)) {
                userName = loginData.getUsername();
                jsonObject.addProperty("Result", "succeed");
                String jsonResult = gson.toJson(jsonObject);
                writer.println(jsonResult);
            } else {
                jsonObject.addProperty("Result", "failed");
                String jsonResult = gson.toJson(jsonObject);
                writer.println(jsonResult);

            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void handleRegister(JsonObject jsonObject) {
        Gson gson = new Gson();
        User userData = gson.fromJson(jsonObject, User.class);
        try {
            UserSL.register(userData);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleProductList() {
        try {
            ProductSL.getAllProducts();
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleAddingWish(JsonObject jsonObject) {
        int productId = jsonObject.get("productId").getAsInt();
        try {
            WishSL.addWish(productId, userName);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleRemovingWish(JsonObject jsonObject) {
        int productId = jsonObject.get("productId").getAsInt();
        try {
            WishSL.removeWish(productId, userName);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleFriendRequestList() {
        JsonObject jsonObject = new JsonObject();
        try {
            ArrayList<FriendDTO> requests = FriendRequestSL.getFriendRequestList(userName);
            if (requests == null || requests.isEmpty()) {
                jsonObject.addProperty("Result", "failed");
            } else {
                jsonObject.addProperty("Result", "succeed");
                jsonObject.add("requests", gson.toJsonTree(requests));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            jsonObject.addProperty("Result", "failed");
        }
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleAcceptingFriendRequest(JsonObject jsonObject) {
        String friendUserName = jsonObject.get("friendUserName").getAsString();
        try {
            FriendRequestSL.acceptFriendRequest(friendUserName, userName);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleRejectingFriendRequest(JsonObject jsonObject) {
        String friendUserName = jsonObject.get("friendUserName").getAsString();

        try {
            FriendRequestSL.rejectFriendRequest(friendUserName, userName);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleFriendList(JsonObject jsonObject) {
        try {
            FriendSL.getFriendList(userName);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleFriendWishList(JsonObject jsonObject) {
        String friendUserName = jsonObject.get("friendUserName").getAsString();
        try {
            WishSL.getWishList(friendUserName);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleRemovingFriend(JsonObject jsonObject) {
        String friendUserName = jsonObject.get("friendUserName").getAsString();
        try {
            FriendSL.removeFriend(friendUserName, userName);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleAddingFriend() {
    }

}
