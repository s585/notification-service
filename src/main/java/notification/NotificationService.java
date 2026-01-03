package notification;

import metrics.MetricsService;
import user.User;
import user.UserRepository;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NotificationService {
    private final UserRepository userRepository;
    private final MetricsService metricsService;
    private final Map<NotificationChannel, NotificationSender> senders;

    public NotificationService(UserRepository userRepository,
                               MetricsService metricsService,
                               Collection<NotificationSender> senders) {
        this.userRepository = userRepository;
        this.metricsService = metricsService;
        this.senders = senders.stream()
            .collect(Collectors.toMap(NotificationSender::channel, Function.identity()));
    }

    void send(Notification notification) {
        userRepository.findById(notification.getUserId())
            .filter(User::isActive)
            .stream()
            .map(User::getActiveChannels)
            .flatMap(Collection::stream)
            .forEach(channel -> sendMessage(channel, notification));
    }

    private void sendMessage(NotificationChannel channel, Notification notification) {
        try {
            NotificationSender sender = senders.get(channel);
            if (sender == null) {
                throw new RuntimeException(channel + " processing not implemented");
            }
            sender.send(notification);
            metricsService.incrementSent(channel);
        } catch (RuntimeException ex) {
            metricsService.incrementFailed(channel);
        }
    }
}
