package metrics;

import notification.NotificationChannel;

public interface MetricsService {
    void incrementSent(NotificationChannel channel);
    void incrementFailed(NotificationChannel channel);
}
