package iuh.fit.se;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Order order = new Order("ORD001");

        order.setStrategy(new StandardShipping());
        order.processOrder();


        order.setStrategy(new ExpressShipping());
        order.processOrder();

        order.setStrategy(new SameDayShipping());
        order.processOrder();
    }
}