package sl;

import dal.DatabaseConnection;
import dal.NotificationDAO;
import dal.UserDAO;
import dto.NotificationDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import server.LoggerUtil;

public class NotificationSL {
     private static final Connection con = DatabaseConnection.getConnection();

     public static void addNotification(String receiverName, NotificationDTO notification) {
          if (receiverName == null || receiverName.trim().isEmpty()) {
               LoggerUtil.error("Invalid input: receiverName is null or empty.");
               return;
          }

          if (notification == null) {
               LoggerUtil.error("Invalid input: notification is null.");
               return;
          }

          if (notification.getNotificationContent() == null || notification.getNotificationContent().trim().isEmpty()) {
               LoggerUtil.error("Invalid input: notification message is null or empty.");
               return;
          }

          if (con == null) {
               LoggerUtil.error("Database connection failed.");
               return;
          }
          try {
               if (UserDAO.countUsers(receiverName, con) != 1) {
                    LoggerUtil.error("Invalid input: Friend user does not exist: " + receiverName);
                    return;
               }
               LoggerUtil.info("Adding notification for user: " + receiverName);
               int result = NotificationDAO.addNotification(receiverName, notification, con);
               if (result == 1) {
                    LoggerUtil.info("Notification added successfully for user: " + receiverName);
               } else {
                    LoggerUtil.error("Failed to add notification for user: " + receiverName);
               }
          } catch (SQLException e) {
               LoggerUtil.error("Database error while adding notification for user: " + receiverName + ". Error: " + e.getMessage());
          }
     }

     public static int removeNotification(NotificationDTO notification) {
          return 0;
     }

     public static ArrayList<NotificationDTO> getNotificationList(String userName) {
          if (userName == null || userName.trim().isEmpty()) {
               LoggerUtil.error("Invalid input: userName is null or empty.");
               return null;
          }
          if (con == null) {
               LoggerUtil.error("Database connection failed.");
               return null;
          }
          try {
               if (UserDAO.countUsers(userName, con) != 1) {
                    LoggerUtil.error("Invalid input: user does not exist: " + userName);
                    return null;
               }
               return NotificationDAO.getAllNotifications(userName, con);
          } catch (SQLException e) {
               LoggerUtil.error("Database error while retrieving notifications for user: " + userName + ". Error: " + e.getMessage());
               return null;
          }
     }
}
