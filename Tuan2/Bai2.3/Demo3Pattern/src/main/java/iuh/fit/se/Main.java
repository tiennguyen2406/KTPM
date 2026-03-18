package iuh.fit.se;

import iuh.fit.se.Decorator.BasicPayment;
import iuh.fit.se.Decorator.Discount;
import iuh.fit.se.Decorator.PaymentComponent;
import iuh.fit.se.Decorator.ProcessingFee;
import iuh.fit.se.State.Payment;
import iuh.fit.se.Strategy.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // Decorator
        PaymentComponent payment = new BasicPayment();
        payment = new ProcessingFee(payment);
        payment = new Discount(payment);

        System.out.println(payment.getDescription());
        System.out.println("Total: " + payment.getCost());

        // Strategy
        PaymentContext context = new PaymentContext();
        context.setStrategy(new CreditCardPayment());
        context.pay(payment.getCost());

        context.setStrategy(new PayPalPayment());
        context.pay(payment.getCost());

        // State
        Payment order = new Payment("P001");
        order.process();
        order.process();
    }
}