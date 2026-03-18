package iuh.fit.se;

public class Order {
    private ShippingStrategy strategy;
    private String orderId;

    public Order(String orderId) {
        this.orderId = orderId;
    }

    public void setStrategy(ShippingStrategy strategy) {
        this.strategy = strategy;
    }

    public void processOrder() {
        if (strategy == null) {
            System.out.println("Chưa chọn phương thức giao hàng!");
        } else {
            strategy.shipOrder(orderId);
        }
    }
}
