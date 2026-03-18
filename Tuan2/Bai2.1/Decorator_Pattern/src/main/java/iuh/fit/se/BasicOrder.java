package iuh.fit.se;

public class BasicOrder implements OrderComponent {
    @Override
    public String getDescription() {
        return "Đơn hàng cơ bản";
    }

    @Override
    public double getCost() {
        return 100.0;
    }
}
