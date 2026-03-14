package iuh.fit.se;

public class EmailNotification implements Notification{
    @Override
    public void send(String message) {
        System.out.println("Gửi Email: " + message);
    }
}
