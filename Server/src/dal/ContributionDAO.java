package dal;

import dto.ContributionDTO;
import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.ArrayList;

public class ContributionDAO {

    static public int Contribute(ContributionDTO Contribution) throws SQLException {

        Database db = new Database();
        Connection con = db.getConnection();
        con.setAutoCommit(false);
        double remain = Contribution.getRemaining();
        double contributionAmount = Contribution.getBalance();
        String userName = Contribution.getContributer_name();
        int wish_id = Contribution.getWish_id();
        try {

            //Step 1: check user balance
            PreparedStatement checkBalance = con.prepareStatement("select balance from users where username = ? ");
            checkBalance.setString(1, userName);
            ResultSet resultBalance = checkBalance.executeQuery();
            if (!resultBalance.next()) {
                System.out.println("User not found!");
                return -1; // User not found
            }
            double userBalance = resultBalance.getDouble("balance");
            if (userBalance < contributionAmount) {
                System.out.println("Insufficient points!");
                return -2;
            }

            //Step 2: Check the remaining balance of the wish
            if (contributionAmount > remain) {
                System.out.println("Contribution exceeds remaining amount");
                return -3;
            }

            // Step 3: Check if the user already contributed
            PreparedStatement checkContribution = con.prepareStatement("select amount from contributions where Contributer_name = ? AND WISH_ID = ?");
            checkContribution.setString(1, userName);
            checkContribution.setInt(2, wish_id);
            ResultSet isexist = checkContribution.executeQuery();

            if (isexist.next()) {
                // Contribution exists, update it
                PreparedStatement updateContribution = con.prepareStatement("update contributions set amount = amount + ? where Contributer_name = ? and wish_id = ? ");
                updateContribution.setInt(3, wish_id);
                updateContribution.setDouble(1, contributionAmount);
                updateContribution.setString(2, userName);
                updateContribution.executeUpdate();

            } else {
                // No contribution exists, insert a new record
                PreparedStatement insertContribution = con.prepareStatement("insert into contributions (Contributer_name, wish_id, amount) values (?,?,?)");
                insertContribution.setString(1, userName);
                insertContribution.setInt(2, wish_id);
                insertContribution.setDouble(3, contributionAmount);

                insertContribution.executeUpdate();
            }

            //Step 4: update points from the contributer's balance
            PreparedStatement updatebalance = con.prepareStatement("update users set balance = balance - ? where username = ?");
            updatebalance.setDouble(1, contributionAmount);
            updatebalance.setString(2, userName);
            updatebalance.executeUpdate();

            Contribution.setRemaining(remain - contributionAmount);
            remain = Contribution.getRemaining();
            if (remain == 0) {
                try (PreparedStatement updateStatus = con.prepareStatement("update wish_table set status = 'Granted' where wish_id =? ")) {
                    updateStatus.setInt(1, wish_id);
                    updateStatus.executeUpdate();
                }
            }
            con.commit();
            return 1;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            db.close();
        }
    }

    static public ArrayList<ContributionDTO> getAllContribution(UserDTO Contribution) throws SQLException {
        return new ArrayList<>();
    }

}
