package iuh.fit.se;

public class Main {
    public static void main(String[] args) {
        // --- TRƯỜNG HỢP 1: THỊ TRƯỜNG CHỨNG KHOÁN ---
        Stock appleStock = new Stock("AAPL");

        // Các nhà đầu tư đăng ký theo dõi
        User investor1 = new User("Nguyễn Văn A");
        User investor2 = new User("Trần Thị B");

        appleStock.attach(investor1);
        appleStock.attach(investor2);

        System.out.println("--- Cập nhật giá cổ phiếu lần 1 ---");
        appleStock.setPrice(150.5);

        // Nhà đầu tư A hủy đăng ký (không muốn nhận tin nữa)
        appleStock.detach(investor1);

        System.out.println("\n--- Cập nhật giá cổ phiếu lần 2 ---");
        appleStock.setPrice(155.0); // Chỉ còn investor2 nhận được thông báo


        // --- TRƯỜNG HỢP 2: QUẢN LÝ DỰ ÁN (TASK) ---
        System.out.println("\n" + "=".repeat(30));
        Task softwareTask = new Task("Thiết kế Database");

        User dev1 = new User("Developer Nam");
        User lead = new User("Team Lead Hùng");

        softwareTask.attach(dev1);
        softwareTask.attach(lead);

        System.out.println("--- Thay đổi trạng thái công việc ---");
        softwareTask.setStatus("ĐANG THỰC HIỆN (In Progress)");

        System.out.println("\n--- Hoàn thành công việc ---");
        softwareTask.setStatus("ĐÃ HOÀN THÀNH (Done)");
    }
}