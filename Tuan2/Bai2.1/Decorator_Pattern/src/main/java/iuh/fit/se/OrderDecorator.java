package iuh.fit.se;

public abstract class OrderDecorator implements OrderComponent {
    protected OrderComponent order;

    public OrderDecorator(OrderComponent order) {
        this.order = order;
    }

    @Override
    public String getDescription() {
        return order.getDescription();
    }

    @Override
    public double getCost() {
        return order.getCost();
    }
}
