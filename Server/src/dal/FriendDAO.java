/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dto.UserDTO;
import dto.FriendDTO;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Ahmed
 */
public class FriendDAO {
    
    
    static public int addFriend(String friendUserName, String userName) throws SQLException{return 0;}
    static public int removeFriend(String friendUserName, String userName) throws SQLException{return 0;}
    static public ArrayList<FriendDTO> getAllFriends(String userName) throws SQLException{return new ArrayList<>();}

}
