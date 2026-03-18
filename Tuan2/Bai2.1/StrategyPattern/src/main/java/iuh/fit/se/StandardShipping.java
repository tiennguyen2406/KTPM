package iuh.fit.se;

public class StandardShipping implements ShippingStrategy {
    @Override
    public void shipOrder(String orderId) {
        System.out.println("Đơn " + orderId + ": Giao hàng tiêu chuẩn (3-5 ngày).");
    }
}
