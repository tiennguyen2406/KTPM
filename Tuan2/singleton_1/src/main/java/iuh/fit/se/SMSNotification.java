package iuh.fit.se;

public class SMSNotification implements Notification{
    @Override
    public void send(String message) {
        System.out.println(" Gửi SMS: " + message);
    }
}
