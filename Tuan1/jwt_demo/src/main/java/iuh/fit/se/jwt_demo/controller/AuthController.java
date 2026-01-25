package iuh.fit.se.jwt_demo.controller;

import iuh.fit.se.jwt_demo.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public Map<String, String> login() {
        // Trong thực tế bạn sẽ kiểm tra username/password ở đây
        String access = tokenService.generateToken("admin", 15);
        String refresh = tokenService.generateToken("admin", 1440); // 24h
        return Map.of("accessToken", access, "refreshToken", refresh);
    }
}