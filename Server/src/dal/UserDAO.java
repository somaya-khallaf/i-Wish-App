/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dto.HomeUserDTO;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import server.LoggerUtil;


public class UserDAO {

    static public String getPassword(String userName, Connection con) throws SQLException {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("password") : null;
            }
        }
    }

    static public int addUser(UserDTO user, Connection con) throws SQLException {
        if (user == null || con == null) {
            LoggerUtil.error("Invalid input: user or database connection is null.");
            return 0;
        }
        String query = "INSERT INTO users (username, full_name, password, gender, phone, balance, dob) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getFull_name());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getGender());
            statement.setString(5, user.getPhone());
            statement.setFloat(6, user.getBalance());
            statement.setDate(7, new java.sql.Date(user.getDob().getTime()));
            int rowsAffected = statement.executeUpdate();
            con.commit();
            return rowsAffected;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        }
    }

    static public HomeUserDTO getHomeUser(String userName, Connection con) throws SQLException {
        String query = "SELECT username, full_name, balance FROM users WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? new HomeUserDTO(rs.getString("username"), rs.getString("full_name"), rs.getDouble("balance")) : null;
            }
        }
    }

    static public int countUsers(String userName, Connection con) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    static public int updateBalance(String userName) throws SQLException {
        return 0;
    }

    static public boolean addToBalance(String username, double points, Connection con) throws SQLException {
        String query = "UPDATE users SET balance = balance + ? WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setDouble(1, points);
            stmt.setString(2, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        }
    }
   public static int changePassword(String user, String newPassword, Connection con) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, newPassword); // Ensure this is a hashed password
            stmt.setString(2, user);

            int updatedRows = stmt.executeUpdate();
            con.commit();

            if (updatedRows > 0) {
                LoggerUtil.info("Password updated successfully for user: " + user);
            } else {
                LoggerUtil.warning("No user found with username: " + user);
            }

            return updatedRows;
        } catch (SQLException e) {
            con.rollback();
            LoggerUtil.severe("Error updating password for user " + user + ": " + e.getMessage());
            throw e;
        }
    }
    
    
}
