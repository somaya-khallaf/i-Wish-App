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
import server.LoggerUtil;


public class FriendRequestDAO {

    public static int sendFriendRequest(String friendUsername, String userName, Connection con) throws SQLException {
        String query = "INSERT INTO Friend_requests (Username, Friendname) VALUES (?, ?)";
        LoggerUtil.info(userName + " is sending a friend request to " + friendUsername);

        try {
            con.setAutoCommit(false);
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, userName);
                stmt.setString(2, friendUsername);
                int rowsInserted = stmt.executeUpdate();
                con.commit();
                return rowsInserted;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static int rejectFriendRequest(String friendUserName, String userName, Connection con) throws SQLException {
        String query = "DELETE FROM friend_requests WHERE FRIENDNAME = ? AND USERNAME = ?";
        try {
            con.setAutoCommit(false);
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, userName);
                stmt.setString(2, friendUserName);
                int rowsDeleted = stmt.executeUpdate();
                con.commit();
                return rowsDeleted;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static int acceptFriendRequest(String friend, String userName, Connection con) throws SQLException {
        String deleteQuery = "DELETE FROM friend_requests WHERE FRIENDNAME = ? AND USERNAME = ?";
        String insertQuery = "INSERT INTO friend (USERNAME, FRIENDNAME) VALUES (?, ?)";

        try {
            con.setAutoCommit(false);
            try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, userName);
                deleteStmt.setString(2, friend);
                int rowsDeleted = deleteStmt.executeUpdate();

                try (PreparedStatement insertStmt1 = con.prepareStatement(insertQuery);
                        PreparedStatement insertStmt2 = con.prepareStatement(insertQuery)) {
                    insertStmt1.setString(1, friend);
                    insertStmt1.setString(2, userName);
                    int rowsInserted1 = insertStmt1.executeUpdate();

                    insertStmt2.setString(1, userName);
                    insertStmt2.setString(2, friend);
                    int rowsInserted2 = insertStmt2.executeUpdate();

                    con.commit();
                    return rowsDeleted + rowsInserted1 + rowsInserted2;
                }
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } finally {
            con.setAutoCommit(true);
        }
    }

    public static ArrayList<FriendDTO> getFriendSuggestions(String friendName, String userName, Connection con) throws SQLException {
        String query = "SELECT us.username, us.full_name FROM users us "
                + "WHERE (us.username = ? OR us.full_name LIKE ? OR us.username LIKE ?) "
                + "AND us.username <> ? "
                + "AND NOT EXISTS (SELECT 1 FROM friend "
                + "WHERE (username = ? AND friendname = us.username) "
                + "OR (username = us.username AND friendname = ?)) "
                + "AND NOT EXISTS (SELECT 1 FROM friend_requests "
                + "WHERE (username = ? AND friendname = us.username) "
                + "OR (username = us.username AND friendname = ?))";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, friendName);
            stmt.setString(2, "%" + friendName + "%");
            stmt.setString(3, "%" + friendName + "%");
            stmt.setString(4, userName);
            stmt.setString(5, userName);
            stmt.setString(6, friendName);
            stmt.setString(7, userName);
            stmt.setString(8, friendName);

            try (ResultSet rs = stmt.executeQuery()) {
                ArrayList<FriendDTO> suggestions = new ArrayList<>();
                while (rs.next()) {
                    suggestions.add(new FriendDTO(rs.getString("username"), rs.getString("full_name")));
                }
                return suggestions;
            }
        }
    }

   public static ArrayList<FriendDTO> getAllFriendRequests(String userName, Connection con) throws SQLException {
    String query = "SELECT FR.USERNAME, U.FULL_NAME FROM friend_requests FR " +
                   "JOIN users U ON FR.USERNAME = U.USERNAME WHERE FR.FRIENDNAME = ?";
    
    try (PreparedStatement stmt = con.prepareStatement(query)) {
        stmt.setString(1, userName);
        try (ResultSet rs = stmt.executeQuery()) {
            ArrayList<FriendDTO> requests = new ArrayList<>();
            while (rs.next()) {
                requests.add(new FriendDTO(rs.getString("USERNAME"), rs.getString("FULL_NAME")));
            }
            return requests;
        }
    }
}


    public static int countFriendRequestsBetween(String userName, String friendName, Connection con) throws SQLException {
        String query = "SELECT COUNT(*) AS numRequests FROM friend_requests WHERE (username = ? AND friendname = ?) OR (username = ? AND friendname = ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userName);
            stmt.setString(2, friendName);
            stmt.setString(3, friendName);
            stmt.setString(4, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("numRequests") : 0;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Retrieve friend request count failed.");
            throw e;
        }
    }
}
