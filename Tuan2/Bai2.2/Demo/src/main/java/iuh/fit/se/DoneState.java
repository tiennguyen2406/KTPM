package iuh.fit.se;

public class DoneState implements OrderState {
    public void handle(Order order) {
        System.out.println("Hoàn thành");
    }
}
