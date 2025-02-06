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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Ahmed
 */
public class FriendRequestDAO {
    
    static public int sendFriendRequest(String friendUserName, String userName) throws SQLException{return 0;}
    static public int rejectFriendRequest(String friendUserName, String userName)throws SQLException{return 0;}
    static public int acceptFriendRequest(String friend, String userName) throws SQLException {return 0;}
    static public ArrayList<FriendDTO> getAllFriendRequests(String userName) throws SQLException{
        ArrayList<FriendDTO> requests = new ArrayList<>();
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt  = con.prepareStatement("select FR.FRIENDNAME , U.FULL_NAME from friend_requests fr join users u on FR.FRIENDNAME=U.USERNAME  where FR.USERNAME = ?");

        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
         while(rs.next()) {
            FriendDTO temp = new FriendDTO(rs.getString("FRIENDNAME"),rs.getString("FULL_NAME"));
            requests.add(temp);
            System.out.println(rs.getString("FRIENDNAME")+" "+rs.getString("FULL_NAME"));
        }
        db.close();
        stmt.close();
        return requests;
    
    }
}
