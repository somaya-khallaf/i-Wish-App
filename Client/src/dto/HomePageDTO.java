package dto;

import java.util.ArrayList;

public class HomePageDTO {
    private UserDTO userDTO;
    private ArrayList<WishDTO> wishList;
    private ArrayList<NotificationDTO> notificationList;

    public HomePageDTO(UserDTO userDTO, ArrayList<WishDTO> wishList, ArrayList<NotificationDTO> notificationList) {
        this.userDTO = userDTO;
        this.wishList = wishList;
        this.notificationList = notificationList;
    }

}
