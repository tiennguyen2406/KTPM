package iuh.fit.se;

public class SameDayShipping implements ShippingStrategy {
    @Override
    public void shipOrder(String orderId) {
        System.out.println("Đơn " + orderId + ": Giao trong ngày.");
    }
}
