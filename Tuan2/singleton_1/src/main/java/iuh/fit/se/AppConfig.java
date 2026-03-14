package iuh.fit.se;

public class AppConfig {
    private static AppConfig instance;
    private String version = "1.0.0";

    private AppConfig() {}

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public void showConfig() {
        System.out.println("Hệ thống phiên bản: " + version);
    }
}
