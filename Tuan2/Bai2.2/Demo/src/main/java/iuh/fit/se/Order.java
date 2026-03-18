package iuh.fit.se;

public class Order {
    private OrderState state;
    private TaxStrategy strategy;
    private Product product;

    public Order(Product product) {
        this.product = product;
        this.state = new NewState();
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public void setStrategy(TaxStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        state.handle(this);

        double price = product.getPrice();
        double tax = strategy.calculateTax(price);

        System.out.println(product.getDescription());
        System.out.println("Price: " + price);
        System.out.println("Tax: " + tax);
        System.out.println("Total: " + (price + tax));
        System.out.println("------");
    }
}
