package dto;

import java.util.ArrayList;

public class HomePageDTO {
    private HomeUserDTO userDTO;
    private ArrayList<WishDTO> wishList;
    private ArrayList<NotificationDTO> notificationList;

    public HomePageDTO(HomeUserDTO userDTO, ArrayList<WishDTO> wishList, ArrayList<NotificationDTO> notificationList) {
        this.userDTO = userDTO;
        this.wishList = wishList;
        this.notificationList = notificationList;
    }

    public void setUserDTO(HomeUserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public void setWishList(ArrayList<WishDTO> wishList) {
        this.wishList = wishList;
    }

    public void setNotificationList(ArrayList<NotificationDTO> notificationList) {
        this.notificationList = notificationList;
    }

    public HomeUserDTO getUserDTO() {
        return userDTO;
    }

    public ArrayList<WishDTO> getWishList() {
        return wishList;
    }

    public ArrayList<NotificationDTO> getNotificationList() {
        return notificationList;
    }

}
