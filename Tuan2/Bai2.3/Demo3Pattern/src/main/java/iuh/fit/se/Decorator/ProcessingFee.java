package iuh.fit.se.Decorator;

public class ProcessingFee extends PaymentDecorator {
    public ProcessingFee(PaymentComponent payment) {
        super(payment);
    }

    public double getCost() {
        return payment.getCost() + 10;
    }

    public String getDescription() {
        return payment.getDescription() + " + Phí xử lý";
    }
}