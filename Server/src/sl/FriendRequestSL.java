package sl;

import dal.FriendRequestDAO;
import dto.FriendDTO;
import dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class FriendRequestSL {

    static public void sendFriendRequest(String friendUsername, String userName) throws SQLException {
        FriendRequestDAO.sendFriendRequest(friendUsername, userName);
    }
    static public int rejectFriendRequest(String friend, String userName) throws SQLException {
        if (friend != null && !friend.isEmpty() && userName != null && !userName.isEmpty()) {
            return FriendRequestDAO.rejectFriendRequest(friend, userName);
        }
        return 0;
    }
    static public int acceptFriendRequest(String friend, String userName) throws SQLException {
        if (friend != null && !friend.isEmpty() && userName != null && !userName.isEmpty()) {
            return FriendRequestDAO.acceptFriendRequest(friend, userName);
        }
        return 0;
    }
    static public ArrayList<FriendDTO> getFriend(String friendName, String userName) throws SQLException {
        if(friendName != null && !friendName.isEmpty() && userName != null && !userName.isEmpty()  )
            return FriendRequestDAO.getFriend(friendName, userName);
        return null;
    }

    static public ArrayList<FriendDTO> getFriendRequestList(String user) throws SQLException {
        if(user != null && !user.isEmpty())
            return FriendRequestDAO.getAllFriendRequests(user);
        return null;
    }
}
