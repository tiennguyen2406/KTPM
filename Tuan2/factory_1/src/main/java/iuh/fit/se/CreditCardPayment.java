package iuh.fit.se;

public class CreditCardPayment implements Payment{
    @Override
    public void pay(double amount) {
        System.out.println("Thanh toán " + amount + " bằng Credit Card");
    }
}
