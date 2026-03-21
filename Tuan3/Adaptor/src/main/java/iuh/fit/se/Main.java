package iuh.fit.se;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo dịch vụ JSON (Adaptee)
        JsonWebService modernService = new JsonWebService();

        // 2. Tạo Adapter để Client có thể dùng XML
        IXmlService adapter = new XmlToJsonAdapter(modernService);

        // 3. Client gửi dữ liệu XML
        String legacyXmlData = "<data>Hello World from Legacy System</data>";

        System.out.println("--- Client bắt đầu gửi yêu cầu ---");
        adapter.processXmlData(legacyXmlData);
        System.out.println("--- Giao dịch hoàn tất ---");
    }
}