package iuh.fit.se;

public class ExpressShipping implements ShippingStrategy {
    @Override
    public void shipOrder(String orderId) {
        System.out.println("Đơn " + orderId + ": Giao hàng nhanh (1-2 ngày).");
    }
}
