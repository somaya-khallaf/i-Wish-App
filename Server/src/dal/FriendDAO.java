/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dto.UserDTO;
import dto.FriendDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Ahmed
 */
public class FriendDAO {


    static public int removeFriend(String friendUserName, String userName) throws SQLException {

        Database db = new Database();
        Connection con = db.getConnection();
        con.setAutoCommit(false);
        try {
            PreparedStatement deleteStmt1 = con.prepareStatement("delete from friend where (FRIENDNAME = ? and USERNAME = ?) ");
            deleteStmt1.setString(1, friendUserName);
            deleteStmt1.setString(2, userName);
            int rs1 = deleteStmt1.executeUpdate();

            PreparedStatement deleteStmt2 = con.prepareStatement("delete from friend where (FRIENDNAME = ? and USERNAME = ?)");
            deleteStmt2.setString(1, userName);
            deleteStmt2.setString(2, friendUserName);

            int rs2 = deleteStmt2.executeUpdate();

            con.commit();
            return rs1 + rs2;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            db.close();
        }
    }

    static public ArrayList<FriendDTO> getAllFriends(String userName) throws SQLException {

        ArrayList<FriendDTO> requests = new ArrayList<>();
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("select U.FULL_NAME , U.USERNAME from friend fr join users u on FR.FRIENDNAME=U.USERNAME  where FR.USERNAME = ? ");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            FriendDTO temp = new FriendDTO(rs.getString("USERNAME"), rs.getString("FULL_NAME"));
            requests.add(temp);
            System.out.println(rs.getString("FULL_NAME") + " " + rs.getString("USERNAME"));
        }
        db.close();
        stmt.close();
        return requests;
    }
}
