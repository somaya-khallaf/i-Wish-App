package sl;

import dal.DatabaseConnection;
import dal.UserDAO;
import dto.HomeUserDTO;
import dto.LoginDTO;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.SQLException;
import server.LoggerUtil;

public class UserSL {

    private static final Connection con = DatabaseConnection.getConnection();

    static public boolean validateLogin(LoginDTO logindto) {
        if (logindto == null || logindto.getUsername() == null || logindto.getUsername().trim().isEmpty()
                || logindto.getPassword() == null || logindto.getPassword().isEmpty()) {
            LoggerUtil.error("Invalid login credentials: username or password is null or empty.");
            return false;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return false;
        }

        try {
            String password = UserDAO.getPassword(logindto.getUsername(), con);
            if (password.equals(logindto.getPassword())) {
                LoggerUtil.info("User " + logindto.getUsername() + " logged in successfully.");
                return true;
            }
            LoggerUtil.warning("Failed login attempt for user: " + logindto.getUsername());
            return false;
        } catch (SQLException e) {
            LoggerUtil.error("Database error during login for user " + logindto.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    static public int register(UserDTO user) {
        if (user == null) {
            LoggerUtil.error("User registration failed: user object is null.");
            return 0;
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()
                || user.getPassword() == null || user.getPassword().trim().isEmpty()
                || user.getFull_name() == null || user.getFull_name().trim().isEmpty()
                || user.getPhone() == null || user.getPhone().trim().isEmpty()
                || user.getDob() == null) {
            LoggerUtil.error("User registration failed: missing required fields.");
            return 0;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return 0;
        }

        try {
            if (UserDAO.countUsers(user.getUsername(), con) != 0) {
                LoggerUtil.error("Invalid input: Friend user already exist: " + user.getUsername());
                return -1;
            }
            int result = UserDAO.addUser(user, con);
            if (result > 0) {
                LoggerUtil.info("User registered successfully: " + user.getUsername());
            } else {
                LoggerUtil.warning("User registration failed: " + user.getUsername());
            }
            return result;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while registering user " + user.getUsername() + ": " + e.getMessage());
            return 0;
        }
    }

    static public HomeUserDTO getUserData(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid username: username is null or empty.");
            return null;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return null;
        }

        try {
            if (UserDAO.countUsers(userName, con) != 1) {
                LoggerUtil.error("Invalid input: Friend user does not exist: " + userName);
                return null;
            }

            LoggerUtil.info("Retrieving user data for username: " + userName);
            HomeUserDTO homeUser = UserDAO.getHomeUser(userName, con);
            if (homeUser == null) {
                LoggerUtil.error("User data not found for username: " + userName);
            }
            return homeUser;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while retrieving user data for " + userName + ": " + e.getMessage());
            return null;
        }
    }

    static public int updateBalance(String userName, double amount) {
        if (userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: userName is null or empty.");
            return 0;
        }

        if (amount <= 0) {
            LoggerUtil.error("Invalid input: amount must be a positive number.");
            return 0;
        }

        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return 0;
        }

        try {
            LoggerUtil.info("Updating balance for user: " + userName + ", amount: " + amount);
            boolean success = UserDAO.addToBalance(userName, amount, con);
            if (success) {
                LoggerUtil.info("Balance updated successfully for user: " + userName);
                return 1;
            } else {
                LoggerUtil.error("Failed to update balance for user: " + userName);
                return 0;
            }
        } catch (SQLException e) {
            LoggerUtil.error("SQL error while updating balance for user: " + userName + ". Error: " + e.getMessage());
            return 0;
        }
    }

    public double getBalance(String userName) {
        return 0;
    }
}
