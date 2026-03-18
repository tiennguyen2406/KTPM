package iuh.fit.se.Strategy;

public class PayPalPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Thanh toán " + amount + " bằng PayPal");
    }
}
