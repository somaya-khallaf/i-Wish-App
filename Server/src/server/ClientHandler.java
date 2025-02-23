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
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.net.ServerSocket;
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
    private JsonObject response;
    private String myToken = "";
    ServerSocket notificationServerSocket;
    static Vector<NotificationData> clients = new Vector<NotificationData>();

    public ClientHandler(Socket socket, ServerSocket notificationServerSocket) {
        try {
            LoggerUtil.info("Connecting to " + socket.getInetAddress() + "...");
            this.socket = socket;
            this.notificationServerSocket = notificationServerSocket;
            writer = new PrintWriter(this.socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            LoggerUtil.info("connected to " + this.socket.getInetAddress());
        } catch (IOException ex) {
            LoggerUtil.error("Failed to connect to " + socket.getInetAddress());
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        response = new JsonObject();
        try {
            String jsonString = reader.readLine();
            if (jsonString == null || jsonString.isEmpty()) {
                LoggerUtil.error("Empty or null JSON string received from client.");
                response.addProperty("Result", "Invalid request");
                writer.println(gson.toJson(response));
                return;
            }
            jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            myToken = jsonObject.has("token") ? jsonObject.get("token").getAsString() : "";
            String command = jsonObject.has("command") ? jsonObject.get("command").getAsString() : "";

            if ("login".equals(command) && myToken.isEmpty()) {

                handleCommand(command);
            } else if (AuthService.validateToken(myToken)) {
                userName = AuthService.getUsernameFromToken(myToken);
                handleCommand(command);
            } else {
                LoggerUtil.error("Invalid token or unauthorized request. Token: " + myToken);
                response.addProperty("Result", "Invalid token or unauthorized");
                writer.println(gson.toJson(response));
            }
        } catch (JsonParseException e) {
            LoggerUtil.error("Error parsing JSON: " + e.getMessage());
            response.addProperty("Result", "Invalid JSON format");
            writer.println(gson.toJson(response));
        } catch (IOException e) {
            LoggerUtil.error("Error reading from client: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("Unexpected error:main " + e.getMessage());
            response.addProperty("Result", "Internal server error");
            writer.println(gson.toJson(response));
        } finally {
            cleanup();
        }
    }

    public void handleCommand(String command) {
        switch (command) {
            case "login":
                handleLogIn();
                break;
            case "getHomePage":
                handleHomePage();
                break;
            case "register":
                handleRegister();
                break;
            case "getProductlist":
                handleProductList();
                break;
            case "addWish":
                handleAddingWish();
                break;
            case "removeWish":
                handleRemovingWish();
                break;
            case "getFriendRequestList":
                handleFriendRequestList();
                break;
            case "acceptFriendRequest":
                handleAcceptingFriendRequest();
                break;
            case "rejectFriendRequest":
                handleRejectingFriendRequest();
                break;
            case "getFriendList":
                handleFriendList();
                break;
            case "getFriendWishList":
                handleFriendWishList();
                break;
            case "removeFriend":
                handleRemovingFriend();
                break;
            case "addFriend":
                handleAddingFriend();
                break;
            case "getFriend":
                handleGettingFriend();
                break;
            case "logout":
                handleLogout();
                break;
            case "contributeToWish":
                handleContribution();
                break;
            case "recharge":
                handleRecharge();
                break;
            case "updatePassword":
                handleUpdatingPassword();
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
            if (cl.getUserName().equals(receiverName)) {
                System.out.println(receiverName + " " + userName);
                cl.getNotificationWriter().println(jsonString);
            }
        }
        NotificationSL.addNotification(receiverName, notification);

    }

    private void handleHomePage() {
        HomeUserDTO userData = UserSL.getUserData(userName);
        ArrayList<WishDTO> wishList = WishSL.getWishList(userName);
        ArrayList<NotificationDTO> NotificationList = NotificationSL.getNotificationList(userName);
        HomePageDTO homePage = new HomePageDTO(userData, wishList, NotificationList);
        response = gson.toJsonTree(homePage).getAsJsonObject();
        writer.println(gson.toJson(response));
    }

    private void handleLogIn() {
        LoginDTO loginData = gson.fromJson(jsonObject.get("data"), LoginDTO.class);
        loginData.setUsername(loginData.getUsername().trim());
        if (UserSL.validateLogin(loginData)) {
            myToken = AuthService.generateToken(loginData.getUsername());
            response.addProperty("token", myToken);
            response.addProperty("Result", "succeed");

        } else {
            response.addProperty("Result", "failed");
        }
        writer.println(gson.toJson(response));
        if (!myToken.isEmpty()) {
            try {
                notificationSocket = notificationServerSocket.accept();
                notificationWriter = new PrintWriter(notificationSocket.getOutputStream(), true);
                notificationReader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()));
                clients.add(new NotificationData(loginData.getUsername(), notificationWriter, notificationSocket));
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void handleRegister() {
        UserDTO userData = gson.fromJson(jsonObject.get("data"), UserDTO.class);
        int result = UserSL.register(userData);
        if (result > 0) {
            response.addProperty("Result", "success");
        } else if (result == -1) {
            response.addProperty("Result", "User already exists");
        } else {
            response.addProperty("Result", "error");
        }
        writer.println(gson.toJson(response));
    }

    private void handleProductList() {
        ArrayList<ProductDTO> products = ProductSL.getAllProducts();
        response.addProperty("Result", "succeed");
        response.add("products", gson.toJsonTree(products));
        writer.println(gson.toJson(response));
    }

    private void handleAddingWish() {
        Integer productId = gson.fromJson(jsonObject.get("data"), Integer.class);
        int success = WishSL.addWish(productId, userName);
        if (success > 0) {
            response.addProperty("Result", "succeed");
        } else {
            response.addProperty("Result", "failed");
            response.addProperty("message", "Failed to add to wishlist");
        }
        writer.println(gson.toJson(response));
    }

    private void handleRemovingWish() {
        Integer[] wishId = gson.fromJson(jsonObject.get("data"), Integer[].class);
        int result = WishSL.removeWish(wishId, userName);
        if (result > 0) {
            response.addProperty("Result", "succeed");
        } else {
            response.addProperty("Result", "failed");
        }
        writer.println(gson.toJson(response));
    }

    private void handleFriendRequestList() {
        ArrayList<FriendDTO> requests = FriendRequestSL.getFriendRequestList(userName);
        if (requests == null || requests.isEmpty()) {
            response.addProperty("Result", "failed");
        } else {
            response.addProperty("Result", "succeed");
            response.add("requests", gson.toJsonTree(requests));
        }
        writer.println(gson.toJson(response));
    }

    private void handleAcceptingFriendRequest() {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        int result = FriendRequestSL.acceptFriendRequest(friendUserName, userName);
        if (result > 0) {
            response.addProperty("Result", "succeed");
            handleAddingNotification(friendUserName, userName + " Accept your friend request.");
        } else {
            response.addProperty("Result", "failed");
        }
        writer.println(gson.toJson(response));
    }

    private void handleRejectingFriendRequest() {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        int result = FriendRequestSL.rejectFriendRequest(friendUserName, userName);
        if (result > 0) {
            response.addProperty("Result", "succeed");
        } else {
            response.addProperty("Result", "failed");
        }
        writer.println(gson.toJson(response));
    }

    private void handleFriendList() {
        ArrayList<FriendDTO> requests = FriendSL.getFriendList(userName);
        if (requests == null || requests.isEmpty()) {
            response.addProperty("Result", "failed");
        } else {
            response.addProperty("Result", "succeed");
            response.add("requests", gson.toJsonTree(requests));
        }
        writer.println(gson.toJson(response));
    }

    private void handleFriendWishList() {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("friendUserName").getAsString();
        ArrayList<WishDTO> requests = WishSL.getWishList(friendUserName);
        if (requests == null || requests.isEmpty()) {
            response.addProperty("Result", "failed");
        } else {
            response.addProperty("Result", "succeed");
            response.add("requests", gson.toJsonTree(requests));
        }
        writer.println(gson.toJson(response));
    }

    private void handleRemovingFriend() {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String friendUserName = dataObject.get("Friendusername").getAsString();
        int result = FriendSL.removeFriend(friendUserName, userName);
        if (result > 0) {
            response.addProperty("Result", "succeed");
        } else {
            response.addProperty("Result", "failed");
        }
        writer.println(gson.toJson(response));
    }

    private void handleAddingFriend() {
        String friendName = gson.fromJson(jsonObject.get("data"), String.class);
        int result = FriendRequestSL.sendFriendRequest(friendName, userName);
        if (result > 0) {
            response.addProperty("Result", "succeed");
            handleAddingNotification(friendName, userName + " sent you a friend request.");
        } else {
            response.addProperty("Result", "failed");
        }
        writer.println(gson.toJson(response));
    }

    private void handleGettingFriend() {
        String friendName = gson.fromJson(jsonObject.get("data"), String.class);
        ArrayList<FriendDTO> requests = FriendRequestSL.getFriendSuggestions(friendName, userName);
        if (requests == null || requests.isEmpty()) {
            response.addProperty("Result", "failed");
        } else {
            response.addProperty("Result", "succeed");
            response.add("requests", gson.toJsonTree(requests));
        }
        writer.println(gson.toJson(response));
    }

    private void handleLogout() {
        writer.println(gson.toJson(response));
        clients.removeIf(client -> client.getUserName().equals(userName));
        LoggerUtil.info("User " + userName + " logged out successfully.");
        cleanup();
    }

    private void handleContribution() {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        int wishId = dataObject.get("wishId").getAsInt();
        double contributionAmount = dataObject.get("contribution").getAsDouble();
        double remaining = dataObject.get("remaining").getAsDouble();
        String friendUsername = dataObject.get("friendUserName").getAsString();
        ContributionDTO contribution = new ContributionDTO(wishId, userName, contributionAmount, remaining, friendUsername);
        int result = ContributionSL.contribute(contribution);
        if (result == 1) {
            response.addProperty("Result", "succeed");
            response.addProperty("Message", "Contribution successful.");
            handleAddingNotification(friendUsername, userName + " contributed to your wish ");
            if (remaining == contributionAmount) {
                handleAddingNotification(friendUsername, "Check your wish list; one of your wishes is completed and ready.");
                ArrayList<String> contributors = ContributionSL.getAllContributors(wishId);
                for (String contributor : contributors) {
                    System.out.println(contributor);
                    handleAddingNotification(contributor,
                            "The product that you contributed to with product name " + dataObject.get("productName").getAsString()
                            + " for user " + friendUsername + " has been GRANTED");
                }
            }
        } else if (result == -1) {
            response.addProperty("Result", "failed");
            response.addProperty("Message", "User not found.");
        } else if (result == -2) {
            response.addProperty("Result", "failed");
            response.addProperty("Message", "Insufficient points.");
        } else if (result == -3) {
            response.addProperty("Result", "failed");
            response.addProperty("Message", "Contribution exceeds remaining amount.");
        } else {
            response.addProperty("Result", "failed");
            response.addProperty("Message", "Failed to contribute.");
        }
        writer.println(gson.toJson(response));
    }

    private void handleRecharge() {
        JsonObject data = jsonObject.get("data").getAsJsonObject();
        double points = data.get("points").getAsDouble();
        String creditCard = data.get("creditCard").getAsString();
        int updateSuccess = UserSL.updateBalance(userName, points);
        if (updateSuccess > 0) {
            response.addProperty("Result", "succeed");
        } else {
            response.addProperty("Result", "failed");
        }
        writer.println(gson.toJson(response));
    }

    private void handleUpdatingPassword() {
        JsonObject dataObject = jsonObject.get("data").getAsJsonObject();
        String oldPassword = dataObject.get("oldPassword").getAsString();
        String newPassword = dataObject.get("newPassword").getAsString();
        int updateSuccess = UserSL.updatePassword(userName, oldPassword, newPassword);
        if (updateSuccess > 0) {
            response.addProperty("Result", "succeed");
        } else if (updateSuccess == -1) {
            response.addProperty("Result", "Incorrect old password");
        } else {
            response.addProperty("Result", "failed");
        }
        writer.println(gson.toJson(response));
    }

    private void cleanup() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            LoggerUtil.error("Error closing resources: " + ex.getMessage());
        }
    }
}
