package notification;

public class PushSender implements NotificationSender {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public void send(Notification notification) {

    }
}
