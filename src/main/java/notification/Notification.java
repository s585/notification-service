package notification;

import java.util.UUID;

public class Notification {
    private UUID id;
    private UUID userId;

    public Notification() {
    }

    public Notification(UUID id, UUID userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    private String message;
}
