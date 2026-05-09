package iuh.fit.cartpu.service;

import iuh.fit.cartpu.entity.CartItem;
import iuh.fit.inventoryservice.entity.Product;
import iuh.fit.cartpu.entity.UserCart;
import iuh.fit.cartpu.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class CartService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public UserCart addToCart(String userId, String productId, int quantity) {
        log.info("🛒 Adding to cart: userId={}, productId={}, quantity={}", userId, productId, quantity);
        
        Object rawProduct = redisTemplate.opsForHash().get("products", productId);
        Product product = toProduct(rawProduct);

        if (product == null) throw new RuntimeException("Sản phẩm không tồn tại!");

        // Lấy tồn kho thực tế từ hash "inventory" (Dùng hàm an toàn để xử lý cả dữ liệu nhị phân lỗi)
        product.setStock(getSafeStock(productId));

        // 2. Lấy giỏ hàng hiện tại (Tự phục hồi nếu dữ liệu cũ lỗi)
        UserCart cart = null;
        try {
            cart = (UserCart) redisTemplate.opsForValue().get("cart:" + userId);
        } catch (Exception e) {
            log.warn("⚠️ Giỏ hàng cũ không tương thích, đang tạo mới... (Lỗi: {})", e.getMessage());
            redisTemplate.delete("cart:" + userId);
        }

        if (cart == null) {
            cart = new UserCart(userId, new ArrayList<>());
        }

        // 3. Cập nhật món hàng
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            cart.getItems().add(new CartItem(productId, product.getName(), product.getPrice(), quantity));
        }

        // 4. Lưu lại vào Redis
        redisTemplate.opsForValue().set("cart:" + userId, cart);
        log.info("✅ Cart updated in Redis for userId: {}. Items count: {}", userId, cart.getItems().size());
        return cart;
    }

    private Product toProduct(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Product p) return p;

        try {
            Class<?> clazz = raw.getClass();
            String id    = (String) clazz.getMethod("getId").invoke(raw);
            String name  = (String) clazz.getMethod("getName").invoke(raw);
            double price = (double) clazz.getMethod("getPrice").invoke(raw);
            int stock    = (int)    clazz.getMethod("getStock").invoke(raw);
            return new Product(id, name, price, stock);
        } catch (Exception e) {
            log.error("❌ Mapping error: {}", e.getMessage());
            return null;
        }
    }

    public void deleteCart(String userId) {
        log.info("🗑️ Deleting cart for userId: {}", userId);
        redisTemplate.delete("cart:" + userId);
    }

    public void updateStock(String productId, int quantity) {
        log.info("🔄 Updating stock for {} in 'inventory' hash: {}", productId, quantity);
        // Lưu dưới dạng String thuần bằng StringRedisTemplate
        stringRedisTemplate.opsForHash().put("inventory", productId, String.valueOf(quantity));
    }

    public UserCart getCart(String userId) {
        log.info("🔍 Fetching cart for userId: {}", userId);
        try {
            UserCart cart = (UserCart) redisTemplate.opsForValue().get("cart:" + userId);
            return cart != null ? cart : new UserCart(userId, new ArrayList<>());
        } catch (Exception e) {
            log.warn("⚠️ Không thể đọc giỏ hàng cũ, trả về giỏ hàng mới. (Lỗi: {})", e.getMessage());
            redisTemplate.delete("cart:" + userId);
            return new UserCart(userId, new ArrayList<>());
        }
    }

    private int getSafeStock(String productId) {
        try {
            // Dùng JDK redisTemplate - nó sẽ tự deserialize chuỗi JDK thành Java String
            Object val = redisTemplate.opsForHash().get("inventory", productId);
            if (val instanceof Integer i) return i;
            if (val instanceof Long l) return l.intValue();
            if (val != null) {
                String s = val.toString().trim().replaceAll("[^0-9-]", "");
                if (!s.isEmpty()) return Integer.parseInt(s);
            }
        } catch (Exception e) {
            log.debug("getSafeStock JDK read failed for {}: {}", productId, e.getMessage());
        }

        try {
            // Fallback: Dùng StringRedisTemplate cho dữ liệu chuỗi thuần
            Object rawStr = stringRedisTemplate.opsForHash().get("inventory", productId);
            if (rawStr != null) {
                String s = rawStr.toString().trim().replaceAll("[^0-9-]", "");
                if (!s.isEmpty()) return Integer.parseInt(s);
            }
        } catch (Exception e) {
            log.error("getSafeStock String read failed for {}: {}", productId, e.getMessage());
        }
        return 0;
    }
}