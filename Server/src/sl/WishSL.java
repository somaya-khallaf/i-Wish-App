package sl;

import dal.WishDAO;
import dto.ProductDTO;
import dto.UserDTO;
import dto.WishDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class WishSL {


    static public int addWish(int productId, String userName) throws SQLException {return 0;}
    static public void removeWish(Integer[] productId, String userName) throws SQLException{
        if (productId == null || productId.length == 0) {
            throw new IllegalArgumentException("Product ID array is empty.");
        }
        int result = WishDAO.removeWish(productId, userName);

    }
    static public ArrayList<WishDTO> getWishList(String userName) throws SQLException{
        ArrayList<WishDTO> wishList = null;
        if (userName != null && !userName.isEmpty())
            wishList = WishDAO.getWishList(userName);
        return wishList;
    }
    static public ArrayList<WishDTO> getWishListFriend(String userName) throws SQLException{
        ArrayList<WishDTO> wishList = null;
        if (userName != null && !userName.isEmpty())
            wishList = WishDAO.getWishListFriend(userName);
         System.out.println("Server response getWishListFriend SL ");
        return wishList;
    }

}
