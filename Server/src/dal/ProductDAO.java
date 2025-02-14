package dal;

import dto.ProductDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import server.LoggerUtil;

public class ProductDAO {

    static public int addProduct(ProductDTO product) throws SQLException {
        return 0;
    }

    static public int removeProduct(ProductDTO product) throws SQLException {
        return 0;
    }

    public static int addProductToWishlist(ProductDTO product, Connection con) throws SQLException {
        String query = "INSERT INTO wishlist (product_id, name, price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, product.getProductId());
            pstmt.setString(2, product.getProductName());
            pstmt.setDouble(3, product.getProductPrice());
            return pstmt.executeUpdate();
        }
    }

    public static ArrayList<ProductDTO> getAllProducts(Connection con) throws SQLException {
        ArrayList<ProductDTO> products = new ArrayList<>();

        try (Statement stmt = con.createStatement()) {
            try {
                stmt.execute("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'");
            } catch (SQLException e) {
                LoggerUtil.error("Failed to alter session: " + e.getMessage());
            }

            String sql = "SELECT PRODUCT_ID, PRODUCT_NAME, PRODUCT_PRICE, MANUFACTURE_DATE FROM product_table";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    products.add(new ProductDTO(
                            rs.getInt("PRODUCT_ID"),
                            rs.getString("PRODUCT_NAME"),
                            rs.getInt("PRODUCT_PRICE"),
                            rs.getString("MANUFACTURE_DATE")
                    ));
                }
                LoggerUtil.info("Retrieved " + products.size() + " products.");
            }
        } catch (SQLException e) {
            LoggerUtil.error("SQL error while retrieving products: " + e.getMessage());
            throw e;
        }
        return products;
    }
}
