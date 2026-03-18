package iuh.fit.se;

public class NewOrderState implements OrderState {
    @Override
    public void handle(Order order) {
        System.out.println("Đơn hàng mới được tạo.");
        order.setState(new ProcessingState());
    }
}
