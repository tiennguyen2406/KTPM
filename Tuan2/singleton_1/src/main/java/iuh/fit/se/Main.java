package iuh.fit.se;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // 1. Kiểm tra Singleton
        AppConfig config = AppConfig.getInstance();
        config.showConfig();

        // 2. Kiểm tra Factory
        NotificationFactory factory = new NotificationFactory();

        Notification n1 = factory.createNotification("EMAIL");
        n1.send("bài tập Design Pattern đã hoàn thành!");

        Notification n2 = factory.createNotification("SMS");
        n2.send("Mã OTP của bạn là 123456");
    }
}