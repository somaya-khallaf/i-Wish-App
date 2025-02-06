package sl;

import dto.FriendDTO;
import dto.UserDTO;
import dal.FriendRequestDAO;
import java.sql.SQLException;
import java.util.ArrayList;

public class FriendRequestSL {

    static public int sendFriendRequest(String friend, String userName) throws SQLException {return 0;}
    static public int rejectFriendRequest(String friend, String userName)throws SQLException{return 0;}
    static public int acceptFriendRequest(String friend, String userName) throws SQLException {return 0;}
    static public ArrayList<FriendDTO> getFriendRequestList(String user) throws SQLException {
        if(user != null && !user.isEmpty())
            return FriendRequestDAO.getAllFriendRequests(user);
        return null;
    }
}
