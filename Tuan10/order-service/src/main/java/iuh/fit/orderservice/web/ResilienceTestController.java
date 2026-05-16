package iuh.fit.orderservice.web;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test endpoints to demonstrate Resilience4j patterns.
 * These endpoints simulate success/failure scenarios to trigger
 * CircuitBreaker, Retry, and RateLimiter behaviors.
 */
@RestController
@RequestMapping("/api/resilience-test")
@Slf4j
public class ResilienceTestController {

    private final AtomicInteger circuitBreakerCallCount = new AtomicInteger(0);
    private final AtomicInteger retryCallCount = new AtomicInteger(0);

    // Whether to simulate failure
    private volatile boolean simulateFailure = false;

    // ===================================================================
    // Control endpoints - toggle failure simulation
    // ===================================================================

    @PostMapping("/toggle-failure")
    public ResponseEntity<Map<String, Object>> toggleFailure(@RequestBody(required = false) Map<String, Boolean> body) {
        if (body != null && body.containsKey("simulate")) {
            simulateFailure = body.get("simulate");
        } else {
            simulateFailure = !simulateFailure;
        }
        log.info("[Test] Failure simulation is now: {}", simulateFailure ? "ON" : "OFF");

        Map<String, Object> response = new HashMap<>();
        response.put("simulateFailure", simulateFailure);
        response.put("message", simulateFailure ? "Failure simulation ENABLED - calls will fail" : "Failure simulation DISABLED - calls will succeed");
        response.put("timestamp", now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("simulateFailure", simulateFailure);
        response.put("circuitBreakerCallCount", circuitBreakerCallCount.get());
        response.put("retryCallCount", retryCallCount.get());
        response.put("timestamp", now());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-counters")
    public ResponseEntity<Map<String, Object>> resetCounters() {
        circuitBreakerCallCount.set(0);
        retryCallCount.set(0);
        simulateFailure = false;
        log.info("[Test] Counters reset and failure simulation disabled");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "All counters reset and failure simulation disabled");
        response.put("timestamp", now());
        return ResponseEntity.ok(response);
    }

    // ===================================================================
    // Test 1: CircuitBreaker
    // ===================================================================

    @GetMapping("/circuit-breaker")
    @CircuitBreaker(name = "userService", fallbackMethod = "circuitBreakerFallback")
    public ResponseEntity<Map<String, Object>> testCircuitBreaker() {
        int count = circuitBreakerCallCount.incrementAndGet();
        log.info("[CircuitBreaker Test] Call #{} - simulateFailure={}", count, simulateFailure);

        if (simulateFailure) {
            throw new RuntimeException("Simulated service failure for CircuitBreaker test (call #" + count + ")");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("callNumber", count);
        response.put("message", "CircuitBreaker test - Call succeeded!");
        response.put("timestamp", now());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> circuitBreakerFallback(Throwable t) {
        log.warn("[CircuitBreaker Test] FALLBACK triggered: {}", t.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("callNumber", circuitBreakerCallCount.get());
        response.put("message", "CircuitBreaker FALLBACK - " + t.getMessage());
        response.put("errorType", t.getClass().getSimpleName());
        response.put("timestamp", now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    // ===================================================================
    // Test 2: Retry
    // ===================================================================

    @GetMapping("/retry")
    @Retry(name = "userService", fallbackMethod = "retryFallback")
    public ResponseEntity<Map<String, Object>> testRetry() {
        int count = retryCallCount.incrementAndGet();
        log.info("[Retry Test] Attempt #{} - simulateFailure={}", count, simulateFailure);

        if (simulateFailure) {
            throw new RuntimeException("Simulated failure for Retry test (attempt #" + count + ")");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("attemptNumber", count);
        response.put("message", "Retry test - Call succeeded on attempt #" + count);
        response.put("timestamp", now());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> retryFallback(Throwable t) {
        log.warn("[Retry Test] ALL RETRIES EXHAUSTED. Fallback: {}", t.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("totalAttempts", retryCallCount.get());
        response.put("message", "Retry FALLBACK - All retry attempts exhausted");
        response.put("errorType", t.getClass().getSimpleName());
        response.put("timestamp", now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    // ===================================================================
    // Test 3: RateLimiter
    // ===================================================================

    @GetMapping("/rate-limiter")
    @RateLimiter(name = "orderCreation", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<Map<String, Object>> testRateLimiter() {
        log.info("[RateLimiter Test] Request accepted");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "RateLimiter test - Request permitted!");
        response.put("timestamp", now());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> rateLimiterFallback(Throwable t) {
        log.warn("[RateLimiter Test] RATE LIMITED: {}", t.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "RATE_LIMITED");
        response.put("message", "RateLimiter FALLBACK - Too many requests! Rate limit exceeded.");
        response.put("errorType", t.getClass().getSimpleName());
        response.put("timestamp", now());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    // ===================================================================
    // Test 4: Combined (CircuitBreaker + Retry + RateLimiter)
    // ===================================================================

    @GetMapping("/combined")
    @CircuitBreaker(name = "foodService", fallbackMethod = "combinedFallback")
    @Retry(name = "foodService", fallbackMethod = "combinedFallback")
    @RateLimiter(name = "orderCreation", fallbackMethod = "combinedFallback")
    public ResponseEntity<Map<String, Object>> testCombined() {
        log.info("[Combined Test] Request processing - simulateFailure={}", simulateFailure);

        if (simulateFailure) {
            throw new RuntimeException("Simulated failure for Combined test");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Combined test - All patterns passed! (CircuitBreaker + Retry + RateLimiter)");
        response.put("timestamp", now());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> combinedFallback(Throwable t) {
        log.warn("[Combined Test] FALLBACK: {}", t.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("message", "Combined FALLBACK - " + t.getMessage());
        response.put("errorType", t.getClass().getSimpleName());
        response.put("timestamp", now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
