package notification;

public interface NotificationSender {
    NotificationChannel channel();
    void send(Notification notification);
}
