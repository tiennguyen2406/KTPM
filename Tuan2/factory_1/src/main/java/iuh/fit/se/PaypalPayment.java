package iuh.fit.se;

public class PaypalPayment implements Payment{
    @Override
    public void pay(double amount) {
        System.out.println("Thanh toán " + amount + " bằng PayPal");
    }
}
