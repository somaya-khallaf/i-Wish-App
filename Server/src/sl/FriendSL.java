package sl;

import dto.FriendDTO;
import dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class FriendSL {

    static public int addFriend(String friendUserName, String userName) throws SQLException {return 0;}
     static public int removeFriend(String friendUserName, String userName) throws SQLException {
       if (friendUserName != null && !friendUserName.isEmpty() && userName != null && !userName.isEmpty()) {
            return FriendDAO.removeFriend(friendUserName, userName);
        }
       System.out.println("Server response removeFriend SL null");
        return 0;
    }

    static public ArrayList<FriendDTO> getFriendList(String userName) throws SQLException {
        if (userName != null && !userName.isEmpty()) {
            System.out.println("Server response getFriendList SL " + FriendDAO.getAllFriends(userName));
            return FriendDAO.getAllFriends(userName);
        }
        System.out.println("Server response getFriendList SL null");
        return null;
    }

}
