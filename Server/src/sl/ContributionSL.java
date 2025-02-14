package sl;

import dal.ContributionDAO;
import dal.DatabaseConnection;
import dal.UserDAO;
import dal.WishDAO;
import dto.ContributionDTO;
import dto.UserDTO;
import java.sql.Connection;

import java.sql.SQLException;
import java.util.ArrayList;
import server.LoggerUtil;

public class ContributionSL {

    public static int contribute(ContributionDTO contribution) {
        if (contribution == null) {
            LoggerUtil.error("Invalid input: Contribution is null.");
            return 0;
        }
        if (contribution.getContributer_name() == null || contribution.getContributer_name().trim().isEmpty()) {
            LoggerUtil.error("Invalid input: Contributer_name is null or empty.");
            return 0;
        }
        if (contribution.getAmount() <= 0) {
            LoggerUtil.error("Invalid input: Amount must be a positive number.");
            return 0;
        }
        if (contribution.getRemaining() <= 0) {
            LoggerUtil.error("Invalid input: Remaining must be a positive number.");
            return 0;
        }
        if (contribution.getFriendUsername() == null || contribution.getFriendUsername().trim().isEmpty()) {
            LoggerUtil.error("Invalid input: FriendUsername is null or empty.");
            return 0;
        }
        if (contribution.getAmount() > contribution.getRemaining()) {
            LoggerUtil.error("Invalid input: The amount must be smaller than the remaining balance.");
            return -3;
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            if (WishDAO.countWishId(contribution.getWish_id(), con) != 1) {
                LoggerUtil.error("Invalid input: Wish does not exist for wish_id: " + contribution.getWish_id());
                return 0;
            }
            if (UserDAO.countUsers(contribution.getContributer_name(), con) != 1) {
                LoggerUtil.error("Invalid input: Contributor does not exist: " + contribution.getContributer_name());
                return -1;
            }
            if (UserDAO.countUsers(contribution.getFriendUsername(), con) != 1) {
                LoggerUtil.error("Invalid input: Friend does not exist: " + contribution.getFriendUsername());
                return -1;
            }

            double balance = ContributionDAO.getBalance(contribution.getContributer_name(), con);
            if (balance < contribution.getAmount()) {
                LoggerUtil.error("Invalid input: Insufficient balance for contributor: " + contribution.getContributer_name());
                return -2;
            }

            LoggerUtil.info("Adding contribution for wish_id: " + contribution.getWish_id());
            int result = ContributionDAO.contribute(contribution, con);
            if (result > 0) {
                LoggerUtil.info("Contribution added successfully for wish_id: " + contribution.getWish_id());
                contribution.setRemaining(contribution.getRemaining() - contribution.getAmount());

                if (contribution.getRemaining() == 0) {
                    ContributionDAO.updateStatus(contribution.getWish_id(), con);  // Fix: No need to store return value
                    LoggerUtil.info("Wish status updated to 'Granted' for wish_id: " + contribution.getWish_id());
                }
                return result;
            } else {
                LoggerUtil.error("Failed to add contribution for wish_id: " + contribution.getWish_id());
                return 0;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Database error while adding contribution for wish_id: " + contribution.getWish_id() + ". Error: " + e.getMessage());
            return 0;
        }
    }

    public ArrayList<ContributionDTO> getAllContribution(UserDTO Contribution) throws SQLException {
        return new ArrayList<>();
    }

}
