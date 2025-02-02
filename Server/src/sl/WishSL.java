package sl;

import dto.ProductDTO;
import dto.UserDTO;
import dto.WishDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class WishSL {


    static public int addWish(int productId, String userName) throws SQLException {return 0;}
    static public int removeWish(int productId, String userName) throws SQLException{return 0;}
    static public ArrayList<WishDTO> getWishList(String userName) throws SQLException{return new ArrayList<>();}

}
