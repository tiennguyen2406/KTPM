package iuh.fit.se.State;

public class Payment {
    private PaymentState state;
    private String paymentId;

    public Payment(String id) {
        this.paymentId = id;
        this.state = new PendingState();
    }

    public void setState(PaymentState state) {
        this.state = state;
    }

    public void process() {
        state.handle(this);
    }
}
