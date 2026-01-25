package iuh.fit.se.jwt_demo;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class RsaKeyGenerator {
    public static void main(String[] args) throws Exception {
        // 1. Tạo cặp khóa RSA 2048 bit
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        // 2. Xác định thư mục lưu file
        String path = "src/main/resources/certs/";
        File directory = new File(path);
        if (!directory.exists()) directory.mkdirs();

        // 3. Ghi Public Key (X.509)
        try (FileOutputStream out = new FileOutputStream(path + "public.pem")) {
            out.write("-----BEGIN PUBLIC KEY-----\n".getBytes());
            out.write(Base64.getMimeEncoder().encode(pair.getPublic().getEncoded()));
            out.write("\n-----END PUBLIC KEY-----".getBytes());
        }

        // 4. Ghi Private Key (PKCS#8 - Chuẩn Spring cần)
        try (FileOutputStream out = new FileOutputStream(path + "private.pem")) {
            out.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
            out.write(Base64.getMimeEncoder().encode(pair.getPrivate().getEncoded()));
            out.write("\n-----END PRIVATE KEY-----".getBytes());
        }

        System.out.println("Thành công! Hãy quay lại IntelliJ và nhấn 'Reload' thư mục resources.");
    }
}