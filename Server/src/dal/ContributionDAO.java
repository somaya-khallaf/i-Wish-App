package dal;

import dto.ContributionDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContributionDAO {

    static public double getBalance(String username, Connection con) throws SQLException {
        String query = "SELECT balance FROM users WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble("balance") : 0.0;
            }
        }
    }

    static public int updateStatus(int wishId, Connection con) throws SQLException {
        String query = "UPDATE wish_table SET status = 'Granted' WHERE wish_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, wishId);
            return stmt.executeUpdate();
        }
    }

    public static int contribute(ContributionDTO contribution, Connection con) throws SQLException {
        String userName = contribution.getContributer_name();
        int wishId = contribution.getWish_id();
        double contributionAmount = contribution.getAmount();

        con.setAutoCommit(false);
        try (
                PreparedStatement updateBalance = con.prepareStatement(
                        "UPDATE users SET balance = balance - ? WHERE username = ?"
                );
                PreparedStatement checkContribution = con.prepareStatement(
                        "SELECT amount FROM contributions WHERE Contributer_name = ? AND wish_id = ?"
                );
                PreparedStatement updateContribution = con.prepareStatement(
                        "UPDATE contributions SET amount = amount + ? WHERE Contributer_name = ? AND wish_id = ?"
                );
                PreparedStatement insertContribution = con.prepareStatement(
                        "INSERT INTO contributions (Contributer_name, wish_id, amount) VALUES (?, ?, ?)"
                )) {
                    updateBalance.setDouble(1, contributionAmount);
                    updateBalance.setString(2, userName);
                    int balanceUpdated = updateBalance.executeUpdate();
                    if (balanceUpdated == 0) {
                        throw new SQLException("Balance update failed. User may not exist or insufficient funds.");
                    }

                    checkContribution.setString(1, userName);
                    checkContribution.setInt(2, wishId);
                    ResultSet result = checkContribution.executeQuery();

                    if (result.next()) {
                        updateContribution.setDouble(1, contributionAmount);
                        updateContribution.setString(2, userName);
                        updateContribution.setInt(3, wishId);
                        updateContribution.executeUpdate();
                    } else {
                        insertContribution.setString(1, userName);
                        insertContribution.setInt(2, wishId);
                        insertContribution.setDouble(3, contributionAmount);
                        insertContribution.executeUpdate();
                    }
                    con.commit();
                    return 1;
                } catch (SQLException e) {
                    con.rollback();
                    throw e;
                } finally {
                    con.setAutoCommit(true);
                }
    }

    public static ArrayList<String> getAllContributors(int wishId, Connection con) throws SQLException {
        ArrayList<String> contributors = new ArrayList<>();

        String query = "SELECT contributer_name FROM contributions WHERE wish_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, wishId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contributors.add(rs.getString("contributer_name"));
                }
            }
        }

        return contributors;
    }

}
