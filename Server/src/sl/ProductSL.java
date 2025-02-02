package sl;

import dto.ProductDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class ProductSL {

     static int addProduct(ProductDTO product) throws SQLException {return 0;}
     static public int removeProduct(ProductDTO product) throws SQLException{return 0;}
     static public ArrayList<ProductDTO> getAllProducts() throws SQLException{return new ArrayList<>();}
}
