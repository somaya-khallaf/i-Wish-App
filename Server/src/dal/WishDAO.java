/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dto.WishDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import server.LoggerUtil;

public class WishDAO {

    static public int addWish(int productId, String userName, Connection con) throws SQLException {
        String query = "INSERT INTO wish_table (product_id, owner_name, status) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.setString(2, userName);
            pstmt.setString(3, "Pending");
            return pstmt.executeUpdate();
        }
    }

    static public int removeWish(Integer[] productId, String userName, Connection con) throws SQLException {
        con.setAutoCommit(false);
        String query = "DELETE FROM wish_table WHERE product_id IN ("
                + String.join(",", Collections.nCopies(productId.length, "?"))
                + ") AND owner_name = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            for (int i = 0; i < productId.length; i++) {
                stmt.setInt(i + 1, productId[i]);
            }
            stmt.setString(productId.length + 1, userName);
            int result = stmt.executeUpdate();
            if (result != productId.length) {
                con.rollback();
                return 0;
            }
            con.commit();
            return result;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        }
    }

    static public ArrayList<WishDTO> getWishList(String userName, Connection con) throws SQLException {

        String query = "WITH ContributionSums AS ( "
                + "SELECT DISTINCT WISH_ID, SUM(AMOUNT) OVER (PARTITION BY WISH_ID) AS TOTAL_CONTRIBUTIONS "
                + "FROM contributions) "
                + "SELECT PT.PRODUCT_ID, WT.WISH_ID, PT.PRODUCT_NAME, PT.PRODUCT_PRICE, "
                + "(PT.PRODUCT_PRICE - COALESCE(CS.TOTAL_CONTRIBUTIONS, 0)) AS REMAINING, WT.STATUS "
                + "FROM wish_table WT "
                + "JOIN product_table PT ON WT.PRODUCT_ID = PT.PRODUCT_ID "
                + "LEFT JOIN ContributionSums CS ON WT.WISH_ID = CS.WISH_ID "
                + "WHERE WT.OWNER_NAME = ? ORDER BY WT.WISH_ID";

        ArrayList<WishDTO> wishList = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    wishList.add(new WishDTO(
                            rs.getInt("PRODUCT_ID"),
                            rs.getInt("WISH_ID"),
                            rs.getString("PRODUCT_NAME"),
                            rs.getDouble("PRODUCT_PRICE"),
                            rs.getDouble("REMAINING"),
                            rs.getString("STATUS")
                    ));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("SQL error while retrieving wishlist: " + e.getMessage());
        }
        return wishList;
    }

    static public int countWishId(int wishId, Connection con) throws SQLException {
        String query = "SELECT COUNT(*) FROM wish_table WHERE wish_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, wishId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
