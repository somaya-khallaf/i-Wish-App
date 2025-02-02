/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dto.FriendDTO;
import dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Ahmed
 */
public class FriendRequestDAO {
    
    static public int sendFriendRequest(String friendUserName, String userName) throws SQLException{return 0;}
    static public int rejectFriendRequest(String friendUserName, String userName)throws SQLException{return 0;}
    static public int acceptFriendRequest(String friend, String userName) throws SQLException {return 0;}
    static public ArrayList<FriendDTO> getAllFriendRequests(String userName) throws SQLException{return new ArrayList<>();}
}
