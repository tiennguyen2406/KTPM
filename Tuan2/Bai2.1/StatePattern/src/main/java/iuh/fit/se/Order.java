package iuh.fit.se;

public class Order {
    private OrderState state;
    private String orderId;

    public Order(String orderId) {
        this.orderId = orderId;
        this.state = new NewOrderState(); // mặc định
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public void process() {
        state.handle(this);
    }

    public String getOrderId() {
        return orderId;
    }
}
