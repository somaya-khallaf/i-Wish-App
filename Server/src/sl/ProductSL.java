package sl;

import dal.DatabaseConnection;
import dal.ProductDAO;
import dto.ProductDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import server.LoggerUtil;

public class ProductSL {

    private static final Connection con = DatabaseConnection.getConnection();

    static public int addProduct(ProductDTO product) throws SQLException {
        return 0;
    }

    static public int removeProduct(ProductDTO product) throws SQLException {
        return 0;
    }

    static public ArrayList<ProductDTO> getAllProducts() {
        if (con == null) {
            LoggerUtil.error("Database connection failed.");
            return null;
        }
        try {
            return ProductDAO.getAllProducts(con);
        } catch (SQLException e) {
            LoggerUtil.error("Database error while retrieving products: " + e.getMessage());
            return null;
        }
    }
}
