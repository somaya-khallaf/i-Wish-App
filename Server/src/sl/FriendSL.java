package sl;

import dal.DatabaseConnection;
import dal.FriendDAO;
import dal.UserDAO;
import dto.FriendDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import server.LoggerUtil;

public class FriendSL {
    private static final Connection con = DatabaseConnection.getConnection();

    public static int removeFriend(String friendUserName, String userName) {
        if (friendUserName == null || friendUserName.trim().isEmpty()
                || userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: friendUserName or userName is null or empty.");
            return 0;
        }

        if (friendUserName.equals(userName)) {
            LoggerUtil.error("Invalid input: Cannot remove yourself as a friend.");
            return 0;
        }

        try {
            if (con == null) {
                LoggerUtil.error("Database connection failed.");
                return 0;
            }
            if (FriendDAO.countFriendBetween(friendUserName, userName, con) != 1) {
                LoggerUtil.error("Friendship does not exist between " + userName + " and " + friendUserName);
                return -1;
            }
            LoggerUtil.info("Removing friend " + friendUserName + " from " + userName + "'s friend list.");
            int result = FriendDAO.removeFriend(friendUserName, userName, con);
            if (result == 2) {
                LoggerUtil.info("Friend " + friendUserName + " removed successfully from " + userName + "'s friend list.");
            } else {
                LoggerUtil.error("Failed to remove friend " + friendUserName + " from " + userName + "'s friend list.");
            }
            return result;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while removing friend " + friendUserName + " from " + userName + "'s friend list. Error: " + e.getMessage());
            return 0;
        }
    }

    public static ArrayList<FriendDTO> getFriendList(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: userName is null or empty.");
            return null;
        }

        try {
            if (con == null) {
                LoggerUtil.error("Database connection failed.");
                return null;
            }
            if (UserDAO.countUsers(userName, con) != 1) {
                LoggerUtil.error("Invalid input: Friend user does not exist: " + userName);
                return null;
            }
            LoggerUtil.info("Retrieving friend list for user: " + userName);
            ArrayList<FriendDTO> friendList = FriendDAO.getAllFriends(userName, con);
            LoggerUtil.info("Friend list retrieved successfully for user: " + userName);
            return friendList;
        } catch (SQLException e) {
            LoggerUtil.error("SQL error while retrieving friend list for user: " + userName + ". Error: " + e.getMessage());
            return null;
        }
    }
}
