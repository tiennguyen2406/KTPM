package iuh.fit.se;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        OrderComponent order = new BasicOrder();

        System.out.println(order.getDescription());
        System.out.println("Cost: " + order.getCost());

        System.out.println("-----");

        order = new GiftWrapDecorator(order);

        order = new InsuranceDecorator(order);

        order = new PriorityDecorator(order);

        System.out.println(order.getDescription());
        System.out.println("Cost: " + order.getCost());
    }
}