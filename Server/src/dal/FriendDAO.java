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
            PreparedStatement stmt = con.prepareStatement("select USERNAME from users where FULL_NAME = ? ");
            stmt.setString(1, friendUserName);
            System.out.println(" removeFriend friendUN DAO" + stmt);
            ResultSet friendUN = stmt.executeQuery();
            if (!friendUN.next()) {
                System.out.println("No user found with full name: " + friendUserName);
                con.rollback();
                return 0; // No deletion performed
            }
            String friendUsername = friendUN.getString("USERNAME");
            System.out.println(" removeFriend friendUN DAO" + friendUsername);

            PreparedStatement deleteStmt1 = con.prepareStatement("delete from friend where (FRIENDNAME = ? and USERNAME = ?) ");
            deleteStmt1.setString(1, friendUsername);
            deleteStmt1.setString(2, userName);
            int rs1 = deleteStmt1.executeUpdate();

            System.out.println(" removeFriend friendUN DAO" + friendUsername);
            PreparedStatement deleteStmt2 = con.prepareStatement("delete from friend where (FRIENDNAME = ? and USERNAME = ?)");
            deleteStmt2.setString(1, userName);
            deleteStmt2.setString(2, friendUsername);

            int rs2 = deleteStmt2.executeUpdate();

            con.commit();

            System.out.println("Result from DAO: " + rs1 + rs2);
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
        PreparedStatement stmt = con.prepareStatement("select U.FULL_NAME from friend fr join users u on FR.FRIENDNAME=U.USERNAME  where FR.USERNAME = ? ");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            FriendDTO temp = new FriendDTO(rs.getString("FULL_NAME"));
            requests.add(temp);
            System.out.println(rs.getString("FULL_NAME"));
        }
        db.close();
        stmt.close();
        return requests;
    }
}
