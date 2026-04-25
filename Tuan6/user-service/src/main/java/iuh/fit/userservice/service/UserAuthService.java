package iuh.fit.userservice.service;

import iuh.fit.userservice.domain.Role;
import iuh.fit.userservice.domain.User;
import iuh.fit.userservice.dto.AuthDtos;
import iuh.fit.userservice.repo.UserRepository;
import iuh.fit.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public AuthDtos.UserResponse register(AuthDtos.RegisterRequest req) {
        log.info("[SERVICE][REGISTER] Bắt đầu đăng ký username='{}'", req.username());
        if (userRepository.existsByUsername(req.username())) {
            log.warn("[SERVICE][REGISTER] Username='{}' đã tồn tại, từ chối đăng ký", req.username());
            throw new IllegalArgumentException("Username already exists");
        }
        Role role = req.role() == null ? Role.USER : req.role();

        User u = new User();
        u.setUsername(req.username());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(role);

        User saved = userRepository.save(u);
        log.info("[SERVICE][REGISTER] Đã tạo người dùng mới: id={}, username='{}', role={}",
                saved.getId(), saved.getUsername(), saved.getRole());
        return new AuthDtos.UserResponse(saved.getId(), saved.getUsername(), saved.getRole());
    }

    @Transactional(readOnly = true)
    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest req) {
        log.info("[SERVICE][LOGIN] Kiểm tra đăng nhập cho username='{}'", req.username());
        User u = userRepository.findByUsername(req.username())
                .orElseThrow(() -> {
                    log.warn("[SERVICE][LOGIN] Không tìm thấy username='{}'", req.username());
                    return new IllegalArgumentException("Invalid credentials");
                });

        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            log.warn("[SERVICE][LOGIN] Mật khẩu sai cho username='{}'", req.username());
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(u.getId(), u.getRole(), u.getUsername());
        log.info("[SERVICE][LOGIN] Tạo JWT thành công cho id={}, username='{}', role={}",
                u.getId(), u.getUsername(), u.getRole());
        return new AuthDtos.LoginResponse(token, new AuthDtos.UserResponse(u.getId(), u.getUsername(), u.getRole()));
    }
}

