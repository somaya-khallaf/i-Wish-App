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


public class WishDAO {

    static public int addWish(int productId, String userName) throws SQLException {
        return 0;
    }

    static public int removeWish(Integer[] productId, String userName) throws SQLException {
        Database db = new Database();
        Connection con = db.getConnection();
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
            System.out.println(result);
            con.commit();
            return result;
        } catch (SQLException e) {
            con.rollback();
            db.close();
            throw e;
        } finally {
            db.close();
        }
    }

    static public ArrayList<WishDTO> getWishList(String userName) throws SQLException {
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("select w.product_id, pr.Product_name, "
                + "pr.Product_price, w.status  from wish_table w join product_table  pr"
                + " on w.product_id = pr.product_id where owner_name = ?");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        ArrayList<WishDTO> wishList = new ArrayList<>();
        while (rs.next()) {
            wishList.add(new WishDTO(rs.getInt("product_id"), rs.getString("product_name"), rs.getString("status"), rs.getDouble("Product_price")));
        }
        db.close();
        stmt.close();
        return wishList;
    }

}
