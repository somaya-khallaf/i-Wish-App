package dal;

import dto.NotificationDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

public class NotificationDAO {

    public static int addNotification(String receiverName, NotificationDTO notification, Connection con) throws SQLException {
        String query = "INSERT INTO notifications (receiver_name, notification_content, notification_date) VALUES (?, ?, ?)";

        try {
            con.setAutoCommit(false);
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, receiverName);
                stmt.setString(2, notification.getNotificationContent());
                stmt.setTimestamp(3, Timestamp.from(notification.getNotificationDate()));
                int rowsInserted = stmt.executeUpdate();
                con.commit();
                return rowsInserted;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } finally {
            con.setAutoCommit(true);
        }
    }

    static public int removeNotification(NotificationDTO notification) {
        return 0;
    }

    static public ArrayList<NotificationDTO> getAllNotifications(String username, Connection con) throws SQLException {
        String query = "SELECT notification_content, notification_date FROM notifications WHERE receiver_name = ? ORDER BY notification_date DESC";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                ArrayList<NotificationDTO> notificationList = new ArrayList<>();
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("notification_date");
                    Instant notificationDate = (timestamp != null) ? timestamp.toInstant() : null;
                    notificationList.add(new NotificationDTO(rs.getString("notification_content"), notificationDate));
                }
                return notificationList;
            }
        }
    }
}
