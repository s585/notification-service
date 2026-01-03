package user;

import notification.NotificationChannel;

import java.util.Set;
import java.util.UUID;

public class User {
    private UUID id;
    private boolean active;
    private Set<NotificationChannel> activeChannels;

    public User(UUID id, boolean active, Set<NotificationChannel> activeChannels) {
        this.id = id;
        this.active = active;
        this.activeChannels = activeChannels;
    }

    public UUID getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public Set<NotificationChannel> getActiveChannels() {
        return activeChannels;
    }
}
