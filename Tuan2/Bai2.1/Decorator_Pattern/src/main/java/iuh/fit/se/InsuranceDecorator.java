package iuh.fit.se;

public class InsuranceDecorator extends OrderDecorator {
    public InsuranceDecorator(OrderComponent order) {
        super(order);
    }

    @Override
    public String getDescription() {
        return order.getDescription() + " + Bảo hiểm";
    }

    @Override
    public double getCost() {
        return order.getCost() + 50.0;
    }
}
