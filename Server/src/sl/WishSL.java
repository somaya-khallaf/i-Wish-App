package sl;

import dal.DatabaseConnection;
import dal.UserDAO;
import dal.WishDAO;
import dto.WishDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import server.LoggerUtil;

public class WishSL {

    private static final Connection con = DatabaseConnection.getConnection();

    static public int addWish(int productId, String userName) {
        if (productId <= 0) {
            LoggerUtil.error("Invalid input: productId must be a positive integer.");
            return 0;
        }

        if (userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: userName is null or empty.");
            return 0;
        }

        try {
            int result = WishDAO.addWish(productId, userName, con);

            if (result > 0) {
                LoggerUtil.info("Wish added successfully: productId=" + productId + ", userName=" + userName);
            } else {
                LoggerUtil.error("Failed to add wish: productId=" + productId + ", userName=" + userName);
            }

            return result;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while adding a wish for userName=" + userName + ". Error: " + e.getMessage());
            return 0;
        }
    }

    static public int removeWish(Integer[] productId, String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: userName is null or empty.");
            return 0;
        }
        if (productId == null || productId.length == 0) {
            LoggerUtil.error("Invalid input: productId is null or empty.");
            return 0;
        }
        try {
            if (con == null) {
                LoggerUtil.error("Database connection failed.");
                return 0;
            }
            int result = WishDAO.removeWish(productId, userName, con);
            if (result > 0) {
                LoggerUtil.info("Successfully removed " + result + " wish(es) for user: " + userName);
            } else {
                LoggerUtil.warning("No wish removed for user: " + userName);
            }
            return result;
        } catch (SQLException e) {
            LoggerUtil.error("Database error while retrieving friend list for user: " + userName + ". Error: " + e.getMessage());
        }
        return 0;
    }

    static public ArrayList<WishDTO> getWishList(String userName) throws SQLException {
        if (userName == null || userName.trim().isEmpty()) {
            LoggerUtil.error("Invalid input: userName is null or empty.");
            return null;
        }
        try {
            if (con == null) {
                LoggerUtil.error("Database connection failed.");
                return null;
            }
            if (UserDAO.countUsers(userName, con) != 1) {
                LoggerUtil.error("Invalid input: user does not exist: " + userName);
                return null;
            }
            return WishDAO.getWishList(userName, con);
        } catch (SQLException e) {
            LoggerUtil.error("Database error while retrieving wishlist: " + userName + ". Error: " + e.getMessage());
            return null;
        }
    }
}
