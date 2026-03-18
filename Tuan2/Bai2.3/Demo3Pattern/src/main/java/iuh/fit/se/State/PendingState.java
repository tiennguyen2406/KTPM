package iuh.fit.se.State;

public class PendingState implements PaymentState {
    public void handle(Payment payment) {
        System.out.println("Thanh toán đang chờ xử lý...");
        payment.setState(new CompletedState());
    }
}
