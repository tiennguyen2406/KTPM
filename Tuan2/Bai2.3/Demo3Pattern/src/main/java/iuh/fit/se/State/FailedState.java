package iuh.fit.se.State;

public class FailedState implements PaymentState {
    public void handle(Payment payment) {
        System.out.println("Thanh toán thất bại!");
    }
}
