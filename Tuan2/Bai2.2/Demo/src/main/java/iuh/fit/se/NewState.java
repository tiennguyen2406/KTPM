package iuh.fit.se;

public class NewState implements OrderState{
    public void handle(Order order) {
        System.out.println("Đơn mới");
        order.setState(new ProcessingState());
    }
}
