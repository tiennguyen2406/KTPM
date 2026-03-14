package iuh.fit.se;

public class NotificationFactory {
    public Notification createNotification(String type) {
        if (type == null || type.isEmpty()) return null;

        return switch (type.toUpperCase()) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SMSNotification();
            default -> throw new IllegalArgumentException("Loại thông báo không hợp lệ: " + type);
        };
    }
}
