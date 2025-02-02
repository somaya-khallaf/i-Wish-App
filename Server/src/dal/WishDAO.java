/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import dto.ProductDTO;
import dto.UserDTO;
import dto.WishDTO;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Ahmed
 */
public class WishDAO {
    
    static public int addWish(int productId, String userName) throws SQLException{return 0;}
    static public int removeWish(int productId, String userName) throws SQLException{return 0;}
    static public ArrayList<WishDTO> getWishList(UserDTO userName) throws SQLException{return new ArrayList<>();}
    
}
