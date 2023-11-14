package com.core.cactus.security;

import com.core.cactus.user.User;
import com.core.cactus.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.core.cactus.security.JwtTokenFilter.validateToken;
import static com.core.cactus.security.SuccessHandler.createToken;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        Optional<User> user = userRepository.findByRefreshToken(refreshToken);
        if (user.isPresent()) {
            validateToken(refreshToken);
            String accessToken = createToken(user.get().getEmail(), 15 * 60 * 1000);
            return ResponseEntity.ok(Collections.singletonMap("access_token", accessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }
}
