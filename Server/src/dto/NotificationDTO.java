package dto;
import java.time.LocalDate;
public class NotificationDTO {
    private String notificationContent;
    private LocalDate notificationDate;

    public NotificationDTO(String notification_content, LocalDate notification_date) {
        this.notificationContent = notification_content;
        this.notificationDate = notification_date;
    }

    public void setNotificationContent(String notification_content) {
        this.notificationContent = notification_content;
    }

    public void setNotificationDate(LocalDate notification_date) {
        this.notificationDate = notification_date;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public LocalDate getNotificationDate() {
        return notificationDate;
    }

}
