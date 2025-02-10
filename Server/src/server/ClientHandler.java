package server;

import dto.*;
import dto.HomePageDTO;

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
import java.time.LocalDate;
import java.util.Vector;
import sl.*;

public class ClientHandler extends Thread {

    private String userName;
    private PrintWriter writer, notificationWriter;
    private BufferedReader reader, notificationReader;
    private Socket socket, notificationSocket;
    private Gson gson = new Gson();
    private JsonObject jsonObject;
    static Vector<NotificationData> clients = new Vector<NotificationData>();

    public ClientHandler(Socket socket, Socket notificationSocket) {
        try {
            this.socket = socket;
            this.notificationSocket = notificationSocket;
            notificationWriter = new PrintWriter(notificationSocket.getOutputStream(), true);
            notificationReader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()));
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
                    notificationWriter.close();
                    notificationSocket.close();
                    socket.close();
                    stop();
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
        } else if (command.equals("getFriend")) {
            handleGettingFriend(jsonObject);
        } else if (command.equals("logout")) {
            handleLogout();
        } else if (command.equals("contributeToWish")) {
            handleContribution();
        }

    }

    private void handleAddingNotification(String receiverName, String notificationContent) {
        NotificationDTO notification = new NotificationDTO(notificationContent, LocalDate.now());
        jsonObject = gson.toJsonTree(notification).getAsJsonObject();
        String jsonString = gson.toJson(jsonObject);
        for (NotificationData cl : clients) {
            if (cl.getUserName().equals(receiverName)) {
                cl.getNotificationWriter().println(jsonString);
                break;
            }
        }
        try {
            NotificationSL.addNotification(receiverName, notification);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
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
        LoginDTO loginData = gson.fromJson(jsonObject.get("data"), LoginDTO.class);

        System.out.println(loginData.getUsername());

        jsonObject = new JsonObject();
        try {
            if (UserSL.logIn(loginData)) {
                userName = loginData.getUsername();
                clients.add(new NotificationData(userName, notificationWriter, notificationSocket));
                System.out.println(userName);
                jsonObject.addProperty("Result", "succeed");

            } else {
                jsonObject.addProperty("Result", "failed");
            }
            String jsonResult = gson.toJson(jsonObject);;
            writer.println(jsonResult);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void handleRegister(JsonObject jsonObject) {
        Gson gson = new Gson();
        User userData = gson.fromJson(jsonObject.get("data"), User.class);
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

    //
    private void handleRemovingWish(JsonObject jsonObject) {
        Integer[] productId = gson.fromJson(jsonObject.get("data"), Integer[].class);
        System.out.println(productId[0]);
        try {
            WishSL.removeWish(productId, userName);
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        jsonObject = new JsonObject();
        jsonObject.addProperty("Result", "succeed");
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
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
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        try {
            int result = FriendRequestSL.acceptFriendRequest(friendUserName, userName);
            jsonObject = new JsonObject();
            if (result > 0) {
                jsonObject.addProperty("Result", "succeed");
                handleAddingNotification(friendUserName, userName + " accepted your friend request.");
            } else {
                jsonObject.addProperty("Result", "failed");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            jsonObject.addProperty("Result", "failed");
        }
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleRejectingFriendRequest(JsonObject jsonObject) {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        try {
            int result = FriendRequestSL.rejectFriendRequest(friendUserName, userName);
            jsonObject = new JsonObject();
            if (result > 0) {
                jsonObject.addProperty("Result", "succeed");
            } else {
                jsonObject.addProperty("Result", "failed");
            }

        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            jsonObject.addProperty("Result", "failed");
        }
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleFriendList(JsonObject jsonObject) {

        jsonObject = new JsonObject();
        try {
            ArrayList<FriendDTO> requests = FriendSL.getFriendList(userName);
            if (requests == null || requests.isEmpty()) {
                jsonObject.addProperty("Result", "failed");
                System.out.println("Server response failed: client handler " + requests);
            } else {
                jsonObject.addProperty("Result", "succeed");
                jsonObject.add("requests", gson.toJsonTree(requests));
                System.out.println("Server response succeed: client handler " + requests);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            jsonObject.addProperty("Result", "failed");
            System.out.println("Server response failed: client handler no result ");
        }
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);

    }

    private void handleFriendWishList(JsonObject jsonObject) {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        try {
            ArrayList<WishDTO> requests = WishSL.getWishListFriend(friendUserName);
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

    private void handleRemovingFriend(JsonObject jsonObject) {

        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendFullName").getAsString();
        System.out.println("Removing friend: " + friendUserName + ", for user: " + userName);

        try {
            int result = FriendSL.removeFriend(friendUserName, userName);
            jsonObject = new JsonObject();
            if (result > 0) {
                jsonObject.addProperty("Result", "succeed");
                System.out.println("Server response: succeed ClientHandler" + result);
            } else {
                jsonObject.addProperty("Result", "failed");
                System.out.println("Server response: failed ClientHandler" + result);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            jsonObject.addProperty("Result", "failed");
            System.out.println("Server response: failed 0 ClientHandler");
        }
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);

    }

    private void handleAddingFriend() {
        try {
            String friendName = gson.fromJson(jsonObject.get("data"), String.class);
            FriendRequestSL.sendFriendRequest(friendName, userName);
            jsonObject = new JsonObject();
            jsonObject.addProperty("Result", "succeed");
            handleAddingNotification(friendName, userName + " sent you a friend request.");
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            jsonObject.addProperty("Result", "failed");
        }
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleGettingFriend(JsonObject jsonObject) {
        try {
            String friendName = gson.fromJson(jsonObject.get("data"), String.class);
            ArrayList<FriendDTO> requests = FriendRequestSL.getFriend(friendName, userName);
            jsonObject = new JsonObject();
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

    private void handleLogout() {
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
        try {
            clients.removeIf(client -> client.getUserName().equals(userName));
            reader.close();
            writer.close();
            notificationWriter.close();
            notificationSocket.close();
            socket.close();
            stop();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
     private void handleContribution() {
        try {
            JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
            int wishId = dataObject.get("wishId").getAsInt();
            double contributionAmount = dataObject.get("contribution").getAsDouble();
            String contributorName = userName;
            double remaining = dataObject.get("remaining").getAsDouble();
            ContributionDTO contribution = new ContributionDTO(wishId, contributorName, contributionAmount, remaining);
            int result = new ContributionSL().Contribute(contribution);
            JsonObject responseJson = new JsonObject();
            if (result == 1) {
                responseJson.addProperty("Result", "succeed");
                responseJson.addProperty("Message", "Contribution successful.");
            } else if (result == -1) {
                responseJson.addProperty("Result", "failed");
                responseJson.addProperty("Message", "User not found.");
            } else if (result == -2) {
                responseJson.addProperty("Result", "failed");
                responseJson.addProperty("Message", "Insufficient points.");
            } else if (result == -3) {
                responseJson.addProperty("Result", "failed");
                responseJson.addProperty("Message", "Contribution exceeds remaining amount.");
            } else {
                responseJson.addProperty("Result", "failed");
                responseJson.addProperty("Message", "Failed to contribute.");
            }

            writer.println(responseJson.toString());
        } catch (SQLException e) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("Result", "failed");
            errorJson.addProperty("Message", "Database error: " + e.getMessage());
            writer.println(errorJson.toString());
        }
    }
}
