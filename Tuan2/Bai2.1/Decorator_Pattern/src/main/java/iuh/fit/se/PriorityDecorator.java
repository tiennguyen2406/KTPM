package iuh.fit.se;

public class PriorityDecorator extends OrderDecorator {
    public PriorityDecorator(OrderComponent order) {
        super(order);
    }

    @Override
    public String getDescription() {
        return order.getDescription() + " + Giao nhanh";
    }

    @Override
    public double getCost() {
        return order.getCost() + 30.0;
    }
}
