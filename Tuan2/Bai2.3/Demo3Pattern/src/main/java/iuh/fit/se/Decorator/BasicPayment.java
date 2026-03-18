package iuh.fit.se.Decorator;

public class BasicPayment implements PaymentComponent {
    public double getCost() {
        return 100;
    }

    public String getDescription() {
        return "Thanh toán cơ bản";
    }
}
