package iuh.fit.se;

public class PaymentFactory {
    public static Payment createPayment(String type) {

        if (type.equalsIgnoreCase("creditcard")) {
            return new CreditCardPayment();
        }

        if (type.equalsIgnoreCase("paypal")) {
            return new PaypalPayment();
        }

        if (type.equalsIgnoreCase("cash")) {
            return new CashPayment();
        }

        return null;
    }
}
