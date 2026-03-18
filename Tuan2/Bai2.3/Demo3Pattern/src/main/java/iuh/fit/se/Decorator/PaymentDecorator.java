package iuh.fit.se.Decorator;

public abstract class PaymentDecorator implements PaymentComponent {
    protected PaymentComponent payment;

    public PaymentDecorator(PaymentComponent payment) {
        this.payment = payment;
    }
}
