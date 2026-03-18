package iuh.fit.se;

public class CancelledState implements OrderState{
    @Override
    public void handle(Order order) {
        System.out.println("Đơn hàng đã bị hủy.");
    }
}
