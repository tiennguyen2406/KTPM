package iuh.fit.orderservice.web;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/demo")
@Slf4j
public class DemoFoodController {

    public record FoodItem(Long id, String name, String description, BigDecimal price,
                           String category, String image, boolean available) {}
    public record OrderRequest(List<OrderItemReq> items) {}
    public record OrderItemReq(Long foodId, int quantity) {}

    private volatile boolean simulateFailure = false;
    private final AtomicInteger callCount = new AtomicInteger(0);
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private final AtomicLong orderIdSeq = new AtomicLong(1000);

    private static final List<FoodItem> FOODS = List.of(
        new FoodItem(1L,"Phở Bò Tái","Phở bò truyền thống với thịt bò tái, nước dùng hầm xương 12 tiếng",new BigDecimal("55000"),"Phở","🍜",true),
        new FoodItem(2L,"Phở Bò Chín","Phở bò chín mềm, nước dùng đậm đà",new BigDecimal("55000"),"Phở","🍜",true),
        new FoodItem(3L,"Phở Gà","Phở gà ta thả vườn, thịt ngọt tự nhiên",new BigDecimal("50000"),"Phở","🐔",true),
        new FoodItem(4L,"Bún Bò Huế","Bún bò Huế cay nồng, đầy đủ chả cua, giò heo",new BigDecimal("60000"),"Bún","🌶️",true),
        new FoodItem(5L,"Bún Chả Hà Nội","Bún chả nướng than hoa kiểu Hà Nội",new BigDecimal("50000"),"Bún","🥩",true),
        new FoodItem(6L,"Cơm Tấm Sườn Bì Chả","Cơm tấm sườn nướng, bì, chả trứng",new BigDecimal("45000"),"Cơm","🍚",true),
        new FoodItem(7L,"Cơm Gà Xối Mỡ","Cơm gà xối mỡ giòn rụm, cơm nấu nước gà",new BigDecimal("50000"),"Cơm","🍗",true),
        new FoodItem(8L,"Bánh Mì Thịt","Bánh mì thịt nguội, pate, rau sống",new BigDecimal("25000"),"Bánh Mì","🥖",true),
        new FoodItem(9L,"Gỏi Cuốn","Gỏi cuốn tôm thịt tươi, chấm tương đậu phộng",new BigDecimal("35000"),"Khai Vị","🥗",true),
        new FoodItem(10L,"Chả Giò","Chả giò chiên giòn nhân thịt heo, nấm mèo",new BigDecimal("40000"),"Khai Vị","🥟",true),
        new FoodItem(11L,"Trà Đá","Trà đá truyền thống",new BigDecimal("5000"),"Đồ Uống","🧊",true),
        new FoodItem(12L,"Nước Mía","Nước mía ép tươi nguyên chất",new BigDecimal("15000"),"Đồ Uống","🥤",true)
    );

    @GetMapping("/foods")
    public ResponseEntity<List<FoodItem>> getAllFoods() {
        return ResponseEntity.ok(FOODS);
    }

    // ===== FAILURE TOGGLE =====
    @PostMapping("/toggle-failure")
    public ResponseEntity<Map<String,Object>> toggleFailure(@RequestBody(required=false) Map<String,Boolean> body) {
        simulateFailure = (body!=null && body.containsKey("simulate")) ? body.get("simulate") : !simulateFailure;
        log.info("[Demo] Failure simulation: {}", simulateFailure?"ON":"OFF");
        return ResponseEntity.ok(Map.of("simulateFailure",simulateFailure,"timestamp",now()));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String,Object>> reset() {
        simulateFailure=false; callCount.set(0); retryCount.set(0);
        return ResponseEntity.ok(Map.of("message","Reset done","timestamp",now()));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String,Object>> status() {
        return ResponseEntity.ok(Map.of("simulateFailure",simulateFailure,"callCount",callCount.get(),"retryCount",retryCount.get()));
    }

    // ===== ORDER WITH RESILIENCE4J =====

    @PostMapping("/order")
    @CircuitBreaker(name = "foodService", fallbackMethod = "orderFallback")
    @Retry(name = "foodService", fallbackMethod = "orderFallback")
    @RateLimiter(name = "orderCreation", fallbackMethod = "orderRateLimitFallback")
    public ResponseEntity<Map<String,Object>> placeOrder(@RequestBody OrderRequest req) {
        int count = callCount.incrementAndGet();
        log.info("[Order] Processing order, call #{}, simulateFailure={}", count, simulateFailure);

        if (simulateFailure) {
            retryCount.incrementAndGet();
            log.warn("[Order] SIMULATED FAILURE on call #{}", count);
            throw new RuntimeException("Simulated: Food Service unavailable (call #" + count + ")");
        }

        // Calculate order
        BigDecimal total = BigDecimal.ZERO;
        List<Map<String,Object>> items = new ArrayList<>();
        for (OrderItemReq item : req.items()) {
            FoodItem food = FOODS.stream().filter(f->f.id().equals(item.foodId())).findFirst().orElse(null);
            if (food == null) continue;
            BigDecimal lineTotal = food.price().multiply(BigDecimal.valueOf(item.quantity()));
            total = total.add(lineTotal);
            items.add(Map.of("name",food.name(),"qty",item.quantity(),"price",food.price(),"lineTotal",lineTotal));
        }

        long orderId = orderIdSeq.getAndIncrement();
        log.info("[Order] ✅ Order #{} placed successfully, total={}", orderId, total);

        Map<String,Object> resp = new LinkedHashMap<>();
        resp.put("status","SUCCESS");
        resp.put("orderId",orderId);
        resp.put("totalAmount",total);
        resp.put("itemCount",items.size());
        resp.put("items",items);
        resp.put("message","Đặt hàng thành công! Order #"+orderId);
        resp.put("patterns","CircuitBreaker ✅ Retry ✅ RateLimiter ✅");
        resp.put("callNumber",count);
        resp.put("timestamp",now());
        return ResponseEntity.ok(resp);
    }

    private ResponseEntity<Map<String,Object>> orderFallback(OrderRequest req, Throwable t) {
        log.error("[Order] FALLBACK (CircuitBreaker/Retry): {}", t.getMessage());
        Map<String,Object> resp = new LinkedHashMap<>();
        resp.put("status","FALLBACK");
        resp.put("errorType",t.getClass().getSimpleName());
        resp.put("message","⚠️ Service lỗi: "+t.getMessage());
        resp.put("hint","CircuitBreaker hoặc Retry đã kích hoạt fallback");
        resp.put("callCount",callCount.get());
        resp.put("retryCount",retryCount.get());
        resp.put("timestamp",now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(resp);
    }

    private ResponseEntity<Map<String,Object>> orderRateLimitFallback(OrderRequest req, Throwable t) {
        log.warn("[Order] RATE LIMITED: {}", t.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
            Map.of("status","RATE_LIMITED","message","🚫 Quá nhiều đơn hàng! Vui lòng chờ.","timestamp",now()));
    }

    // ===== BURST TEST (for RateLimiter demo) =====
    @GetMapping("/ping")
    @RateLimiter(name = "orderCreation", fallbackMethod = "pingFallback")
    public ResponseEntity<Map<String,Object>> ping() {
        return ResponseEntity.ok(Map.of("status","SUCCESS","message","Ping OK","timestamp",now()));
    }
    private ResponseEntity<Map<String,Object>> pingFallback(Throwable t) {
        return ResponseEntity.status(429).body(Map.of("status","RATE_LIMITED","message","🚫 Rate limit!","timestamp",now()));
    }

    private String now() { return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")); }
}
