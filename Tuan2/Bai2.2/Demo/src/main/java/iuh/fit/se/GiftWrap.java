package iuh.fit.se;

public class GiftWrap extends ProductDecorator{
    public GiftWrap(Product product) { super(product); }

    public double getPrice() { return product.getPrice() + 10; }
    public String getDescription() {
        return product.getDescription() + " + Gói quà";
    }
}
