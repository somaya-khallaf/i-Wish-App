/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dto.FriendDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FriendDAO {

    static public int addFriend(String friendUserName, String userName) throws SQLException {
        return 0;
    }

    public static int removeFriend(String friendUserName, String userName, Connection con) throws SQLException {
        String query = "DELETE FROM friend WHERE (FRIENDNAME = ? AND USERNAME = ?)";
        try {
            con.setAutoCommit(false);
            try (PreparedStatement deleteStmt1 = con.prepareStatement(query);
                 PreparedStatement deleteStmt2 = con.prepareStatement(query)) {

                deleteStmt1.setString(1, friendUserName);
                deleteStmt1.setString(2, userName);
                int rowsDeleted1 = deleteStmt1.executeUpdate();

                deleteStmt2.setString(1, userName);
                deleteStmt2.setString(2, friendUserName);
                int rowsDeleted2 = deleteStmt2.executeUpdate();

                con.commit();
                return rowsDeleted1 + rowsDeleted2;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } finally {
            con.setAutoCommit(true);
        }
    }

    static public ArrayList<FriendDTO> getAllFriends(String userName, Connection con) throws SQLException {

        ArrayList<FriendDTO> friendsList = new ArrayList<>();
        String query = "SELECT U.FULL_NAME, U.USERNAME "
                + "FROM friend FR "
                + "JOIN users U ON FR.FRIENDNAME = U.USERNAME "
                + "WHERE FR.USERNAME = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friendsList.add(new FriendDTO(rs.getString("USERNAME"), rs.getString("FULL_NAME")));
                }
            }
        }

        return friendsList;
    }

    public static int countFriendBetween(String userName, String friendName, Connection con) throws SQLException {
        String query = "SELECT COUNT(*) AS numRequests FROM friend WHERE "
                + "(username = ? AND friendname = ?) OR (username = ? AND friendname = ?)";
        try {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, userName);
                stmt.setString(2, friendName);
                stmt.setString(3, userName);
                stmt.setString(4, friendName);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getInt("numRequests") : 0;
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
