package iuh.fit.orderservice.web;

import iuh.fit.orderservice.dto.OrderDtos;
import iuh.fit.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderDtos.OrderResponse create(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody OrderDtos.CreateOrderRequest req
    ) {
        log.info("Received request to create order. Header X-User-Id: {}", userIdHeader);
        return orderService.createOrder(userIdHeader, authorization, req);
    }

    @GetMapping
    public List<OrderDtos.OrderResponse> list(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        return orderService.listOrders(userIdHeader, role);
    }

    @PutMapping("/{id}/status")
    public OrderDtos.OrderResponse updateStatus(
            @RequestHeader(value = "X-Service-Token", required = false) String token,
            @PathVariable Long id,
            @Valid @RequestBody OrderDtos.UpdateStatusRequest req
    ) {
        return orderService.updateOrderStatus(token, id, req);
    }
}
