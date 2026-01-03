package notification;

public class SmsSender implements NotificationSender {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }

    @Override
    public void send(Notification notification) {

    }
}
