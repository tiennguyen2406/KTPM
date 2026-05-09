package iuh.fit.cartpu.controller;

import iuh.fit.cartpu.entity.UserCart;
import iuh.fit.cartpu.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private RestTemplate restTemplate;

    private final String AUTH_SERVICE_URL = "http://192.168.137.180:8080/api/auth/me";

    private String getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(AUTH_SERVICE_URL, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("username");
            }
        } catch (Exception e) {
            log.error("❌ Auth check failed: {}", e.getMessage());
        }
        return null;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestHeader(value = "Authorization", required = false) String token,
                                       @RequestBody Map<String, Object> payload) {
        String userId = getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yêu cầu đăng nhập (Token không hợp lệ)");
        }
        
        String productId = (String) payload.get("productId");
        int quantity = Integer.parseInt(payload.get("quantity").toString());

        log.info("📥 API POST /cart/add: userId={}, productId={}, quantity={}", userId, productId, quantity);
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(value = "Authorization", required = false) String token) {
        String userId = getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yêu cầu đăng nhập (Token không hợp lệ)");
        }
        log.info("📥 API GET /cart: userId={}", userId);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestHeader(value = "Authorization", required = false) String token) {
        String userId = getUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yêu cầu đăng nhập");
        }
        cartService.deleteCart(userId);
        return ResponseEntity.ok(Map.of("message", "Giỏ hàng đã được xóa sạch"));
    }

    @PostMapping("/stock")
    public ResponseEntity<?> updateStock(@RequestParam String productId, @RequestParam int quantity) {
        cartService.updateStock(productId, quantity);
        return ResponseEntity.ok(Map.of("message", "Cập nhật tồn kho thành công", "productId", productId, "stock", quantity));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserCart> getCartByPath(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }
}
