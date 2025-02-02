package dal;

import dto.FriendDTO;
import dto.NotificationDTO;
import dto.UserDTO;

import java.util.ArrayList;

public class NotificationDAO {


    static public int addNotification(NotificationDTO notification) {return 0;}
    static public int removeNotification(NotificationDTO notification) {return 0;}
    static public ArrayList<NotificationDTO> getAllNotifications(UserDTO user) {return new ArrayList<>();}


}
