package iuh.fit.se.State;

public class CompletedState implements PaymentState {
    public void handle(Payment payment) {
        System.out.println("Thanh toán thành công!");
    }
}
