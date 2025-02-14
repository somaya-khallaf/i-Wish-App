package sl;

import dal.DatabaseConnection;
import dal.FriendDAO;
import dal.FriendRequestDAO;
import dal.UserDAO;
import dto.FriendDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import server.LoggerUtil;

public class FriendRequestSL {

    private static final Connection con = DatabaseConnection.getConnection();

    static public void sendFriendRequest(String friendUserName, String userName) {
        if (friendUserName == null || friendUserName.trim().isEmpty()
                || userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: friendUsername or userName is null or empty.");
            return;
        }
        if (friendUserName.equals(userName)) {
            LoggerUtil.error("Invalid input: Cannot send friend request to yourself.");
            return;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return;
        }

        try {
            if (UserDAO.countUsers(userName, con) != 1) {
                LoggerUtil.error("Invalid input: Sender user does not exist: " + userName);
                return;
            }

            if (UserDAO.countUsers(friendUserName, con) != 1) {
                LoggerUtil.error("Invalid input: Friend user does not exist: " + friendUserName);
                return;
            }

            if (FriendRequestDAO.countFriendRequestsBetween(friendUserName, userName, con) != 0) {
                LoggerUtil.error("Friend request already exists between " + userName + " and " + friendUserName);
                return;
            }

            LoggerUtil.info("Sending friend request from " + userName + " to " + friendUserName);
            FriendRequestDAO.sendFriendRequest(friendUserName, userName, con);
            LoggerUtil.info("Friend request sent successfully from " + userName + " to " + friendUserName);
        } catch (SQLException e) {
            LoggerUtil.error("Database error while sending friend request from " + userName + " to " + friendUserName + ". Error: " + e.getMessage());
        }
    }

    static public int rejectFriendRequest(String friendUserName, String userName) {
        if (friendUserName == null || friendUserName.trim().isEmpty() || userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: friend or userName is null or empty.");
            return 0;
        }
        if (friendUserName.equals(userName)) {
            LoggerUtil.error("Invalid input: Cannot reject friend request from yourself.");
            return 0;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return 0;
        }

        try {
            if (FriendRequestDAO.countFriendRequestsBetween(friendUserName, userName, con) != 1) {
                LoggerUtil.error("Friend request does not exist between " + userName + " and " + friendUserName);
                return 0;
            }

            LoggerUtil.info("Rejecting friend request from " + friendUserName + " to " + userName);
            int result = FriendRequestDAO.rejectFriendRequest(friendUserName, userName, con);
            if (result > 0) {
                LoggerUtil.info("Friend request rejected successfully from " + friendUserName + " to " + userName);
            } else {
                LoggerUtil.error("Failed to reject friend request from " + friendUserName + " to " + userName);
            }
            return result;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while rejecting friend request from " + friendUserName + " to " + userName + ". Error: " + e.getMessage());
            return 0;
        }
    }

    static public int acceptFriendRequest(String friendUserName, String userName) {
        if (friendUserName == null || friendUserName.trim().isEmpty() || userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: friend or userName is null or empty.");
            return 0;
        }
        if (friendUserName.equals(userName)) {
            LoggerUtil.error("Invalid input: Cannot accept friend request from yourself.");
            return 0;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return 0;
        }

        try {
            if (FriendRequestDAO.countFriendRequestsBetween(friendUserName, userName, con) != 1) {
                LoggerUtil.error("Friend request does not exist between " + userName + " and " + friendUserName);
                return 0;
            }

            if (FriendDAO.countFriendBetween(friendUserName, userName, con) != 0) {
                LoggerUtil.error("Friendship already exists between " + userName + " and " + friendUserName);
                return 0;
            }

            LoggerUtil.info("Accepting friend request from " + friendUserName + " to " + userName);
            int result = FriendRequestDAO.acceptFriendRequest(friendUserName, userName, con);
            if (result == 3) {
                LoggerUtil.info("Friend request accepted successfully from " + friendUserName + " to " + userName);
            } else {
                LoggerUtil.error("Failed to accept friend request from " + friendUserName + " to " + userName);
            }
            return result;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while accepting friend request from " + friendUserName + " to " + userName + ". Error: " + e.getMessage());
            return 0;
        }
    }

    static public ArrayList<FriendDTO> getFriendSuggestions(String friendUserName, String userName) {
        if (friendUserName == null || friendUserName.trim().isEmpty() || userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: friend or userName is null or empty.");
            return null;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return null;
        }

        try {
            ArrayList<FriendDTO> friends = FriendRequestDAO.getFriendSuggestions(friendUserName, userName, con);
            LoggerUtil.info("Retrieved " + friends.size() + " friend suggestions for " + userName);
            return friends;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while retrieving friend suggestions: " + e.getMessage());
            return null;
        }
    }

    static public ArrayList<FriendDTO> getFriendRequestList(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: userName is null or empty.");
            return null;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return null;
        }

        try {
            ArrayList<FriendDTO> requests = FriendRequestDAO.getAllFriendRequests(userName, con);
            if (requests.isEmpty()) {
                LoggerUtil.info("No pending friend requests for " + userName);
            } else {
                LoggerUtil.info("Retrieved " + requests.size() + " friend requests for " + userName);
            }
            return requests;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while retrieving friend requests: " + e.getMessage());
            return null;
        }
    }
}
