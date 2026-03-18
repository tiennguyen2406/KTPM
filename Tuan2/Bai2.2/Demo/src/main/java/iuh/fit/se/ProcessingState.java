package iuh.fit.se;

public class ProcessingState implements OrderState {
    public void handle(Order order) {
        System.out.println("Đang xử lý");
        order.setState(new DoneState());
    }
}
