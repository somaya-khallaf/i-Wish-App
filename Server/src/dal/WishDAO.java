
package dal;

import dto.FriendDTO;
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
        PreparedStatement stmt = con.prepareStatement("WITH ContributionSums AS ( "
                + "SELECT distinct WISH_ID, SUM(AMOUNT) over (partition by WISH_ID) AS TOTAL_CONTRIBUTIONS"
                + " FROM  contributions) "
                + "SELECT PT.PRODUCT_ID, WT.WISH_ID,PT.PRODUCT_NAME, PT.PRODUCT_PRICE, (PT.PRODUCT_PRICE - COALESCE(CS.TOTAL_CONTRIBUTIONS, 0)) "
                + "AS REMAINING, WT.STATUS FROM wish_table WT "
                + "JOIN product_table PT ON WT.PRODUCT_ID = PT.PRODUCT_ID "
                + "LEFT JOIN  ContributionSums CS ON WT.WISH_ID = CS.WISH_ID "
                + "WHERE WT.OWNER_NAME = ? ");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        ArrayList<WishDTO> wishList = new ArrayList<>();
        while (rs.next()) {
            wishList.add(new WishDTO(rs.getInt("PRODUCT_ID"), rs.getInt("WISH_ID"), rs.getString("PRODUCT_NAME"), rs.getDouble("PRODUCT_PRICE"), rs.getDouble("REMAINING"), rs.getString("STATUS")));
            }
        db.close();
        stmt.close();
        return wishList;
    }

}
