package dto;
import java.time.Instant;

public class NotificationDTO {
    private String notificationContent;
    private Instant notificationDate;

    public NotificationDTO(String notification_content, Instant notification_date) {
        this.notificationContent = notification_content;
        this.notificationDate = notification_date;
    }

    public void setNotificationContent(String notification_content) {
        this.notificationContent = notification_content;
    }

    public void setNotificationDate(Instant notification_date) {
        this.notificationDate = notification_date;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public Instant getNotificationDate() {
        return notificationDate;
    }

}
