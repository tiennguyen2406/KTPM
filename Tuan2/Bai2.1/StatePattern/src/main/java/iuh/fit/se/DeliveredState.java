package iuh.fit.se;

public class DeliveredState implements OrderState{
    @Override
    public void handle(Order order) {
        System.out.println("Đơn hàng đã được giao.");
    }
}
