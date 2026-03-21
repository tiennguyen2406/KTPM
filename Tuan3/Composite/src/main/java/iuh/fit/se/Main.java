package iuh.fit.se;

public class Main {
    public static void main(String[] args) {
        // Tạo các file đơn lẻ
        File file1 = new File("BaoCao.docx", 500);
        File file2 = new File("HinhAnh.png", 1200);
        File file3 = new File("Code.java", 15);

        // Tạo thư mục và thêm file
        Folder subFolder = new Folder("TaiLieuHocTap");
        subFolder.addComponent(file1);
        subFolder.addComponent(file3);

        Folder rootFolder = new Folder("C: Drive");
        rootFolder.addComponent(file2);
        rootFolder.addComponent(subFolder);

        // Hiển thị cấu trúc cây
        rootFolder.showDetails("");
    }
}