package dal;

import dto.ProductDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class ProductDAO {

    static public int addProduct(ProductDTO product) throws SQLException {return 0;}
    static public int removeProduct(ProductDTO product) throws SQLException{return 0;}
    static public ArrayList<ProductDTO> getAllProducts() throws SQLException{return new ArrayList<>();}


}
