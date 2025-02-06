package sl;

import dal.NotificationDAO;
import dto.NotificationDTO;
import dto.UserDTO;
import java.sql.SQLException;

import java.util.ArrayList;

public class NotificationSL {

     static public void addNotification(String receiverName, NotificationDTO notification) throws SQLException {
          NotificationDAO.addNotification(receiverName, notification);
     }

     public int removeNotification(NotificationDTO notification) {
          return 0;
     }

     static public ArrayList<NotificationDTO> getNotificationList(String userName) throws SQLException {
          ArrayList<NotificationDTO> notificationList =null;
          if (userName != null && !userName.isEmpty())
               notificationList = NotificationDAO.getAllNotifications(userName);
          return notificationList;
     }

}
