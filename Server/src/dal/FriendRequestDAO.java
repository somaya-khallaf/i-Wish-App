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

/**
 *
 * @author Ahmed
 */
public class FriendRequestDAO {



    static public int sendFriendRequest(String friendUsername, String userName) throws SQLException {
        Database db = new Database();
        Connection con = null;
        try {
            con = db.getConnection();
            con.setAutoCommit(false);
            String query = "INSERT INTO Friend_requests (Username, Friendname) VALUES (?, ?)";
            try (PreparedStatement insertStmt = con.prepareStatement(query)) {
                insertStmt.setString(1, userName);
                insertStmt.setString(2, friendUsername);
                int rowsInserted = insertStmt.executeUpdate();

                if (rowsInserted > 0) {
                    con.commit();
                    System.out.println("Friend request sent successfully.");
                    return rowsInserted;
                } else {
                    con.rollback();
                    System.out.println("Failed to send friend request.");
                    return 0;
                }
            } catch (SQLException e) {
                con.rollback();
                System.err.println("Error sending friend request: " + e.getMessage());
                throw e;
            }
        } finally {
            if (con != null) {
                db.close();
            }
        }
    }



    static public int rejectFriendRequest(String friendUserName, String userName) throws SQLException {
        return 0;
    }

    static public int acceptFriendRequest(String friendUsername, String userName) throws SQLException {
        /*Database db = new Database();
            Connection con = db.getConnection();
            PreparedStatement insertFriendStmt = con.prepareStatement("insert into friend (Username, Friendname) values (?, ?);"
                    + "insert into friend (friendname, username) values (?, ?)");
            insertFriendStmt.setString(1, userName);
            insertFriendStmt.setString(2, friend);
            insertFriendStmt.setString(3, userName);
            insertFriendStmt.setString(4, friend);
            insertFriendStmt.executeUpdate();

            PreparedStatement deleteRequestStmt = con.prepareStatement("delete from Friend_requests where "
                    + "(Username = ? and Friendname = ?) or (Username = ? and Friendname = ?)");
            deleteRequestStmt.setString(1, userName);
            deleteRequestStmt.setString(2, friend);
            deleteRequestStmt.setString(3, friend);
            deleteRequestStmt.setString(4, userName);
            deleteRequestStmt.executeUpdate();
            db.close();
            insertFriendStmt.close();
            deleteRequestStmt.close();*/
        return 0;

    }

    static public ArrayList<FriendDTO> getFriend(String friendName, String userName) throws SQLException {
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement(
                "select us.username, us.full_name "
                        + "from users us "
                        + "where (us.username = ? or us.full_name like ? or us.username like ?) "
                        + "and us.username <> ? "
                        + "and not exists ( "
                        + "select 1 from friend "
                        + "where (username = ? and friendname = us.username) "
                        + "or (username = us.username and friendname = ?) "
                        + ") "
                        + "and not exists ( "
                        + "select 1 from friend_requests "
                        + "where (username = ? and friendname = us.username) "
                        + "or (username = us.username and friendname = ?) "
                        + ")"
        );
        stmt.setString(1, friendName);
        stmt.setString(2, "%" + friendName + "%");
        stmt.setString(3, "%" + friendName + "%");
        stmt.setString(4, userName);
        stmt.setString(5, userName);
        stmt.setString(6, friendName);
        stmt.setString(7, userName);
        stmt.setString(8, friendName);
        ResultSet rs = stmt.executeQuery();
        ArrayList<FriendDTO> requests = new ArrayList<>();
        while (rs.next()) {
            requests.add(new FriendDTO(rs.getString("username"), rs.getString("full_name")));
        }
        db.close();
        stmt.close();
        return requests;

    }

    static public ArrayList<FriendDTO> getAllFriendRequests(String userName) throws SQLException {
        ArrayList<FriendDTO> requests = new ArrayList<>();
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("select FR.FRIENDNAME , U.FULL_NAME from friend_requests fr join users u on FR.FRIENDNAME=U.USERNAME  where FR.USERNAME = ?");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            FriendDTO temp = new FriendDTO(rs.getString("FRIENDNAME"), rs.getString("FULL_NAME"));
            requests.add(temp);
        }
        db.close();
        stmt.close();
        return requests;

    }
}
