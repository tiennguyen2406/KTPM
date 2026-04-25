package iuh.fit.userservice.web;

import iuh.fit.userservice.domain.Role;
import iuh.fit.userservice.dto.AuthDtos;
import iuh.fit.userservice.repo.UserRepository;
import iuh.fit.userservice.security.JwtService;
import iuh.fit.userservice.service.UserAuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserAuthService userAuthService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public AuthDtos.UserResponse register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
        log.info("[REGISTER] Nhận yêu cầu đăng ký cho username='{}'", req.username());
        AuthDtos.UserResponse response = userAuthService.register(req);
        log.info("[REGISTER] Đăng ký thành công: id={}, username='{}', role={}",
                response.id(), response.username(), response.role());
        return response;
    }

    @PostMapping("/login")
    public AuthDtos.LoginResponse login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        log.info("[LOGIN] Nhận yêu cầu đăng nhập cho username='{}'", req.username());
        try {
            AuthDtos.LoginResponse response = userAuthService.login(req);
            log.info("[LOGIN] Đăng nhập thành công: id={}, username='{}', role={}",
                    response.user().id(), response.user().username(), response.user().role());
            return response;
        } catch (IllegalArgumentException e) {
            log.warn("[LOGIN] Đăng nhập thất bại cho username='{}': {}", req.username(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/users")
    public List<AuthDtos.UserResponse> listUsers(
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        log.info("[LIST_USERS] Nhận yêu cầu lấy danh sách người dùng, role header='{}'", role);
        if (!Role.ADMIN.name().equals(role)) {
            log.warn("[LIST_USERS] Truy cập bị từ chối - role='{}' không có quyền ADMIN", role);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN only");
        }
        List<AuthDtos.UserResponse> users = userRepository.findAll()
                .stream()
                .map(u -> new AuthDtos.UserResponse(u.getId(), u.getUsername(), u.getRole()))
                .toList();
        log.info("[LIST_USERS] Trả về {} người dùng", users.size());
        return users;
    }

    @GetMapping("/validate")
    public AuthDtos.ValidateResponse validate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        log.debug("[VALIDATE] Nhận yêu cầu xác thực token");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("[VALIDATE] Xác thực thất bại: không có token hoặc sai định dạng");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing token");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        Claims c;
        try {
            c = jwtService.parse(token);
        } catch (Exception e) {
            log.warn("[VALIDATE] Token không hợp lệ: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        Long id = Long.valueOf(c.getSubject());
        String username = String.valueOf(c.get("username"));
        Role role = Role.valueOf(String.valueOf(c.get("role")));
        log.info("[VALIDATE] Token hợp lệ: id={}, username='{}', role={}", id, username, role);
        return new AuthDtos.ValidateResponse(id, username, role);
    }
}

