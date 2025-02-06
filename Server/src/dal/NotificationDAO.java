package dal;

import dto.FriendDTO;
import dto.NotificationDTO;
import dto.UserDTO;
import dto.WishDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class NotificationDAO {

    static public int addNotification(String receiverName, NotificationDTO notification) throws SQLException {
        Database db = new Database();
        Connection con = db.getConnection();
        con.setAutoCommit(false);
        String query = "insert into notifications (receiver_name, notification_content, notification_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, receiverName);
            stmt.setString(2, notification.getNotificationContent());
            LocalDateTime localDateTime = notification.getNotificationDate().atStartOfDay();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            stmt.setTimestamp(3, timestamp);
            int result = stmt.executeUpdate();
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

    static public int removeNotification(NotificationDTO notification) {
        return 0;
    }

    static public ArrayList<NotificationDTO> getAllNotifications(String username) throws SQLException {
        Database db = new Database();
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("select notification_content, notification_date  from notifications "
                + "where receiver_name = ? order by notification_date Desc");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        ArrayList<NotificationDTO> notificationList = new ArrayList<>();
        while (rs.next()) {
            LocalDate notification_date = rs.getDate("notification_date").toLocalDate();
            notificationList.add(new NotificationDTO(rs.getString("notification_content"), notification_date));
        }
        stmt.close();
        db.close();
        return notificationList;
    }

}
