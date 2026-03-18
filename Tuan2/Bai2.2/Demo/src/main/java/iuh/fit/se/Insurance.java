package iuh.fit.se;

public class Insurance extends ProductDecorator {
    public Insurance(Product product) { super(product); }

    public double getPrice() { return product.getPrice() + 20; }
    public String getDescription() {
        return product.getDescription() + " + Bảo hiểm";
    }
}
