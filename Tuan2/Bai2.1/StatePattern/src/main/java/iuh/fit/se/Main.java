package iuh.fit.se;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Order order = new Order("ORD001");

        // State 1: New
        order.process();

        // State 2: Processing
        order.process();

        // State 3: Delivered
        order.process();

        // Test hủy đơn
        Order order2 = new Order("ORD002");
        order2.setState(new CancelledState());
        order2.process();
    }
}