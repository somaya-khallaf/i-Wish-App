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

import java.sql.SQLException;

import java.time.Instant;
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
            LoggerUtil.info("Connecting to " + socket.getInetAddress() + "...");
            this.socket = socket;
            this.notificationSocket = notificationSocket;
            notificationWriter = new PrintWriter(notificationSocket.getOutputStream(), true);
            notificationReader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            LoggerUtil.info("connected to " + socket.getInetAddress());
            start();
        } catch (IOException ex) {
            LoggerUtil.error("Failed to connect to " + socket.getInetAddress());
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
                    clients.removeIf(client -> client.getUserName().equals(userName));
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
        switch (command) {
            case "login":
                handleLogIn(jsonObject);
                break;
            case "getHomePage":
                handleHomePage();
                break;
            case "register":
                handleRegister(jsonObject);
                break;
            case "getProductlist":
                handleProductList();
                break;
            case "addWish":
                handleAddingWish(jsonObject);
                break;
            case "removeWish":
                handleRemovingWish(jsonObject);
                break;
            case "getFriendRequestList":
                handleFriendRequestList();
                break;
            case "acceptFriendRequest":
                handleAcceptingFriendRequest(jsonObject);
                break;
            case "rejectFriendRequest":
                handleRejectingFriendRequest(jsonObject);
                break;
            case "getFriendList":
                handleFriendList(jsonObject);
                break;
            case "getFriendWishList":
                handleFriendWishList(jsonObject);
                break;
            case "removeFriend":
                handleRemovingFriend(jsonObject);
                break;
            case "addFriend":
                handleAddingFriend();
                break;
            case "getFriend":
                handleGettingFriend(jsonObject);
                break;
            case "logout":
                handleLogout();
                break;
            case "contributeToWish":
                handleContribution();
                break;
            case "recharge":
                handleRecharge(jsonObject);
                break;
            default:
                break;
        }

    }

    private void handleAddingNotification(String receiverName, String notificationContent) {
        NotificationDTO notification = new NotificationDTO(notificationContent, Instant.now());
        jsonObject = gson.toJsonTree(notification).getAsJsonObject();
        String jsonString = gson.toJson(jsonObject);
        for (NotificationData cl : clients) {
            System.out.println(cl.getUserName());
            if (cl.getUserName().equals(receiverName)) {
                cl.getNotificationWriter().println(jsonString);
            }
        }
        NotificationSL.addNotification(receiverName, notification);

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
        loginData.setUsername(loginData.getUsername().trim());
        jsonObject = new JsonObject();
        if (UserSL.validateLogin(loginData)) {
            userName = loginData.getUsername();
            clients.add(new NotificationData(userName, notificationWriter, notificationSocket));
            jsonObject.addProperty("Result", "succeed");

        } else {
            jsonObject.addProperty("Result", "failed");
        }
        String jsonResult = gson.toJson(jsonObject);;
        writer.println(jsonResult);

    }

    private void handleRegister(JsonObject jsonObject) {
        Gson gson = new Gson();
        UserDTO userData = gson.fromJson(jsonObject.get("data"), UserDTO.class);
        int result = UserSL.register(userData);
        jsonObject = new JsonObject();
        if (result > 0) {
            jsonObject.addProperty("Result", "success");
        } else if (result == -1) {
            jsonObject.addProperty("Result", "User already exists");
        } else {
            jsonObject.addProperty("Result", "error");
        }
        writer.println(gson.toJson(jsonObject));
    }

    private void handleProductList() {

        ArrayList<ProductDTO> products = ProductSL.getAllProducts();
        JsonObject response = new JsonObject();
        System.out.println(products.get(0));
        response.addProperty("Result", "succeed");
        response.add("products", gson.toJsonTree(products));
        writer.println(gson.toJson(response));

    }

    private void handleAddingWish(JsonObject jsonObject) {
        try {
            //int productId = jsonObject.get("product_id").getAsInt();
            Integer productId = gson.fromJson(jsonObject.get("data"), Integer.class);
            int success = WishSL.addWish(productId, userName);

            JsonObject response = new JsonObject();
            if (success > 1) {
                response.addProperty("Result", "succeed");
            } else {
                response.addProperty("Result", "failed");
                response.addProperty("message", "Failed to add to wishlist");
            }
            writer.println(gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("Result", "failed");
            errorResponse.addProperty("message", "An error occurred while adding to wishlist");
            writer.println(gson.toJson(errorResponse));
        }
    }

    //
    private void handleRemovingWish(JsonObject jsonObject) {
        Integer[] productId = gson.fromJson(jsonObject.get("data"), Integer[].class);
        int result = WishSL.removeWish(productId, userName);
        jsonObject = new JsonObject();
        if (result > 0) {
            jsonObject.addProperty("Result", "succeed");
        } else {
            jsonObject.addProperty("Result", "failed");
        }

        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleFriendRequestList() {
        JsonObject jsonObject = new JsonObject();
        ArrayList<FriendDTO> requests = FriendRequestSL.getFriendRequestList(userName);
        if (requests == null || requests.isEmpty()) {
            jsonObject.addProperty("Result", "failed");
        } else {
            jsonObject.addProperty("Result", "succeed");
            jsonObject.add("requests", gson.toJsonTree(requests));
        }

        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleAcceptingFriendRequest(JsonObject jsonObject) {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        int result = FriendRequestSL.acceptFriendRequest(friendUserName, userName);
        jsonObject = new JsonObject();
        if (result > 0) {
            jsonObject.addProperty("Result", "succeed");
        } else {
            jsonObject.addProperty("Result", "failed");
        }

        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleRejectingFriendRequest(JsonObject jsonObject) {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        int result = FriendRequestSL.rejectFriendRequest(friendUserName, userName);
        jsonObject = new JsonObject();
        if (result > 0) {
            jsonObject.addProperty("Result", "succeed");
        } else {
            jsonObject.addProperty("Result", "failed");
        }

        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleFriendList(JsonObject jsonObject) {

        jsonObject = new JsonObject();
        ArrayList<FriendDTO> requests = FriendSL.getFriendList(userName);
        if (requests == null || requests.isEmpty()) {
            jsonObject.addProperty("Result", "failed");
            System.out.println("Server response failed: client handler " + requests);
        } else {
            jsonObject.addProperty("Result", "succeed");
            jsonObject.add("requests", gson.toJsonTree(requests));
            System.out.println("Server response succeed: client handler " + requests);
        }

        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);

    }

    private void handleFriendWishList(JsonObject jsonObject) {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        try {
            ArrayList<WishDTO> requests = WishSL.getWishList(friendUserName);
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
        String friendUserName = dataObject.get("Friendusername").getAsString();
        System.out.println("Removing friend: " + friendUserName + ", for user: " + userName);

        int result = FriendSL.removeFriend(friendUserName, userName);
        jsonObject = new JsonObject();
        if (result > 0) {
            jsonObject.addProperty("Result", "succeed");
            System.out.println("Server response: succeed ClientHandler" + result);
        } else {
            jsonObject.addProperty("Result", "failed");
            System.out.println("Server response: failed ClientHandler" + result);
        }

        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);

    }

    private void handleAddingFriend() {
        String friendName = gson.fromJson(jsonObject.get("data"), String.class);
        FriendRequestSL.sendFriendRequest(friendName, userName);
        jsonObject = new JsonObject();
        jsonObject.addProperty("Result", "succeed");
        handleAddingNotification(friendName, userName + " sent you a friend request.");

        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleGettingFriend(JsonObject jsonObject) {
        String friendName = gson.fromJson(jsonObject.get("data"), String.class);
        ArrayList<FriendDTO> requests = FriendRequestSL.getFriendSuggestions(friendName, userName);
        jsonObject = new JsonObject();
        if (requests == null || requests.isEmpty()) {
            jsonObject.addProperty("Result", "failed");
        } else {
            jsonObject.addProperty("Result", "succeed");
            jsonObject.add("requests", gson.toJsonTree(requests));
        }

        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);
    }

    private void handleLogout() {
        jsonObject = new JsonObject();
        String jsonString = gson.toJson(jsonObject);
        writer.println(jsonString);

        try {
            clients.removeIf(client -> client.getUserName().equals(userName));

            reader.close();
            writer.close();
            notificationWriter.close();
            notificationSocket.close();
            socket.close();
            LoggerUtil.info("User " + userName + " logged out successfully.");
            stop();
        } catch (IOException ex) {
            LoggerUtil.error("Error while logging out user " + userName + ": " + ex.getMessage());
        }
    }

    private void handleContribution() {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        int wishId = dataObject.get("wishId").getAsInt();
        double contributionAmount = dataObject.get("contribution").getAsDouble();
        String contributorName = userName;
        double remaining = dataObject.get("remaining").getAsDouble();
        String friendUsername = dataObject.get("friendUserName").getAsString();
        ContributionDTO contribution = new ContributionDTO(wishId, contributorName, contributionAmount, remaining, friendUsername);
        int result = ContributionSL.contribute(contribution);
        JsonObject responseJson = new JsonObject();
        if (result == 1) {
            responseJson.addProperty("Result", "succeed");
            responseJson.addProperty("Message", "Contribution successful.");
            handleAddingNotification(friendUsername, userName + " contributed to your wish with amount " + contributionAmount);
            if (remaining == contributionAmount) {
                handleAddingNotification(friendUsername, "Check your wish list; one of your wishes is completed and ready.");
            }
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

    }

    private void handleRecharge(JsonObject jsonObject) {
        JsonObject data = jsonObject.get("data").getAsJsonObject();
        double points = data.get("points").getAsDouble();
        String creditCard = data.get("creditCard").getAsString();
        JsonObject response = new JsonObject();
        int updateSuccess = UserSL.updateBalance(userName, points);
        if (updateSuccess > 0) {
            response.addProperty("Result", "succeed");
        } else {
            response.addProperty("Result", "failed");
        }

        writer.println(gson.toJson(response));

    }
}
