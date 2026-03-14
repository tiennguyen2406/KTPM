package iuh.fit.se;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        Payment payment;

        payment = PaymentFactory.createPayment("paypal");
        payment.pay(200);

        payment = PaymentFactory.createPayment("cash");
        payment.pay(100);
    }
}