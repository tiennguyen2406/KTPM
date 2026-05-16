package iuh.fit.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import iuh.fit.orderservice.client.FoodClient;
import iuh.fit.orderservice.client.UserClient;
import iuh.fit.orderservice.domain.Order;
import iuh.fit.orderservice.domain.OrderItem;
import iuh.fit.orderservice.domain.OrderStatus;
import iuh.fit.orderservice.dto.OrderDtos;
import iuh.fit.orderservice.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Service layer for Order operations with Resilience4j patterns applied:
 * - CircuitBreaker: Protects against cascading failures when calling external services
 * - Retry: Automatically retries failed calls to external services
 * - RateLimiter: Limits the number of calls per time period
 *
 * Aspect order: Retry ( CircuitBreaker ( RateLimiter ( Function ) ) )
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final FoodClient foodClient;

    @Value("${internal.service-token:service-token}")
    private String serviceToken;

    // ===================================================================
    // Validate user via User Service (with CircuitBreaker + Retry)
    // ===================================================================

    @CircuitBreaker(name = "userService", fallbackMethod = "validateUserFallback")
    @Retry(name = "userService", fallbackMethod = "validateUserFallback")
    public UserClient.ValidateResponse validateUser(String authorization) {
        log.info("[Resilience4j] Calling User Service to validate token...");
        UserClient.ValidateResponse response = userClient.validate(authorization);
        log.info("[Resilience4j] User validated successfully. User ID: {}, Role: {}", response.id(), response.role());
        return response;
    }

    /**
     * Fallback method when User Service is unavailable.
     * CircuitBreaker is OPEN or Retry exhausted.
     */
    private UserClient.ValidateResponse validateUserFallback(String authorization, Throwable t) {
        log.error("[Resilience4j] User Service unavailable. CircuitBreaker/Retry fallback triggered. Error: {}", t.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "User Service is currently unavailable. Please try again later.");
    }

    // ===================================================================
    // Get food details via Food Service (with CircuitBreaker + Retry)
    // ===================================================================

    @CircuitBreaker(name = "foodService", fallbackMethod = "getFoodFallback")
    @Retry(name = "foodService", fallbackMethod = "getFoodFallback")
    public FoodClient.FoodResponse getFood(Long foodId) {
        log.info("[Resilience4j] Calling Food Service to get details for foodId: {}", foodId);
        FoodClient.FoodResponse response = foodClient.getFood(foodId);
        log.info("[Resilience4j] Food details retrieved: {}", response);
        return response;
    }

    /**
     * Fallback method when Food Service is unavailable.
     * CircuitBreaker is OPEN or Retry exhausted.
     */
    private FoodClient.FoodResponse getFoodFallback(Long foodId, Throwable t) {
        log.error("[Resilience4j] Food Service unavailable for foodId: {}. Error: {}", foodId, t.getMessage());
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Food Service is currently unavailable. Cannot retrieve food #" + foodId + ". Please try again later.");
    }

    // ===================================================================
    // Create order (with RateLimiter)
    // ===================================================================

    @RateLimiter(name = "orderCreation", fallbackMethod = "createOrderFallback")
    public OrderDtos.OrderResponse createOrder(String userIdHeader, String authorization, OrderDtos.CreateOrderRequest req) {
        log.info("[Resilience4j] Creating order - RateLimiter applied. Header X-User-Id: {}", userIdHeader);

        if (userIdHeader == null || authorization == null) {
            log.warn("Missing X-User-Id or Authorization header");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing auth");
        }

        // Call User Service with CircuitBreaker + Retry
        UserClient.ValidateResponse validated = validateUser(authorization);
        Long userId = validated.id();

        if (!String.valueOf(userId).equals(userIdHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User mismatch");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(Instant.now());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderDtos.CreateOrderItem itemReq : req.items()) {
            // Call Food Service with CircuitBreaker + Retry
            FoodClient.FoodResponse food = getFood(itemReq.foodId());

            BigDecimal line = food.price().multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setFoodId(food.id());
            item.setFoodName(food.name());
            item.setUnitPrice(food.price());
            item.setQuantity(itemReq.quantity());
            item.setLineTotal(line);

            order.getItems().add(item);
            total = total.add(line);
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        log.info("[Resilience4j] Order created successfully with ID: {}, Total Amount: {}", saved.getId(), saved.getTotalAmount());
        return toResponse(saved);
    }

    /**
     * Fallback when rate limit is exceeded for order creation.
     */
    private OrderDtos.OrderResponse createOrderFallback(String userIdHeader, String authorization,
                                                         OrderDtos.CreateOrderRequest req, Throwable t) {
        log.warn("[Resilience4j] Rate limit exceeded for order creation. Error: {}", t.getMessage());
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                "Too many order creation requests. Please try again later.");
    }

    // ===================================================================
    // List orders (with RateLimiter)
    // ===================================================================

    @RateLimiter(name = "orderQuery", fallbackMethod = "listOrdersFallback")
    public List<OrderDtos.OrderResponse> listOrders(String userIdHeader, String role) {
        log.info("[Resilience4j] Listing orders - RateLimiter applied.");

        if ("ADMIN".equals(role)) {
            return orderRepository.findAll().stream().map(this::toResponse).toList();
        }
        if (userIdHeader == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing auth");
        }
        Long userId = Long.valueOf(userIdHeader);
        return orderRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    /**
     * Fallback when rate limit is exceeded for order queries.
     */
    private List<OrderDtos.OrderResponse> listOrdersFallback(String userIdHeader, String role, Throwable t) {
        log.warn("[Resilience4j] Rate limit exceeded for order query. Error: {}", t.getMessage());
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                "Too many order query requests. Please try again later.");
    }

    // ===================================================================
    // Update order status (internal service call)
    // ===================================================================

    public OrderDtos.OrderResponse updateOrderStatus(String token, Long id, OrderDtos.UpdateStatusRequest req) {
        log.info("Received request to update order {} status to {}", id, req.status());
        if (token == null || !token.equals(serviceToken)) {
            log.warn("Invalid or missing X-Service-Token");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        Order o = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });
        o.setStatus(req.status());
        Order saved = orderRepository.save(o);
        log.info("Order {} status updated successfully to {}", id, saved.getStatus());
        return toResponse(saved);
    }

    // ===================================================================
    // Helper
    // ===================================================================

    private OrderDtos.OrderResponse toResponse(Order o) {
        List<OrderDtos.OrderItemResponse> items = o.getItems().stream()
                .map(i -> new OrderDtos.OrderItemResponse(i.getFoodId(), i.getFoodName(), i.getUnitPrice(), i.getQuantity(), i.getLineTotal()))
                .toList();
        return new OrderDtos.OrderResponse(o.getId(), o.getUserId(), o.getStatus(), o.getTotalAmount(), o.getCreatedAt(), items);
    }
}
