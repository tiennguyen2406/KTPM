package iuh.fit.se;

public class XmlToJsonAdapter implements IXmlService{
    private JsonWebService jsonWebService;

    public XmlToJsonAdapter(JsonWebService service) {
        this.jsonWebService = service;
    }

    @Override
    public void processXmlData(String xmlData) {
        System.out.println("[Adapter] Đang nhận dữ liệu XML: " + xmlData);

        // Giả lập logic chuyển đổi XML -> JSON
        // Trong thực tế bạn có thể dùng thư viện như Jackson hoặc org.json
        String jsonData = convertXmlToJson(xmlData);

        System.out.println("[Adapter] Đã chuyển đổi sang JSON thành công.");

        // Gọi dịch vụ JSON thực tế
        jsonWebService.sendRequest(jsonData);
    }

    private String convertXmlToJson(String xml) {
        // Logic mô phỏng: lấy nội dung trong thẻ <data>
        String content = xml.replace("<data>", "").replace("</data>", "").trim();
        return "{ \"message\": \"" + content + "\" }";
    }
}
