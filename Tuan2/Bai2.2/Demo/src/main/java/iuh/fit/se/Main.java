package iuh.fit.se;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Product product = new BasicProduct();
        product = new GiftWrap(product);
        product = new Insurance(product);

        Order order = new Order(product);

        order.setStrategy(new VATStrategy());

        order.process();
        order.process();
        order.process();

        System.out.println("=== Đổi thuế ===");
        order.setStrategy(new LuxuryTaxStrategy());
        order.process();
    }
}