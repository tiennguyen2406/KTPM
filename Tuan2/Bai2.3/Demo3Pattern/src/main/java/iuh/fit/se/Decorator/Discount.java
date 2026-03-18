package iuh.fit.se.Decorator;

public class Discount extends PaymentDecorator{
    public Discount(PaymentComponent payment) {
        super(payment);
    }

    public double getCost() {
        return payment.getCost() - 20;
    }

    public String getDescription() {
        return payment.getDescription() + " + Giảm giá";
    }
}
