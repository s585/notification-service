package notification;

public class EmailSender implements NotificationSender {
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(Notification notification) {

    }
}
