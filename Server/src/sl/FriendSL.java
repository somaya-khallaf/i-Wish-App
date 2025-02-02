package sl;

import dto.FriendDTO;
import dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class FriendSL {

    static public int addFriend(String friendUserName, String userName) throws SQLException {return 0;}
    static public int removeFriend(String friendUserName, String userName) throws SQLException{return 0;}
    static public ArrayList<FriendDTO> getFriendList(String userName) throws SQLException{return new ArrayList<>();}

}
