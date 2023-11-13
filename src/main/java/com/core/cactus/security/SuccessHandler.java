package com.core.cactus.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.core.cactus.user.User;
import com.core.cactus.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Component
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        User userEntity;
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            User newUser = User.builder()
                    .email(email)
                    .username(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
            userRepository.save(newUser);
            userEntity = newUser;
        } else {
            userEntity = user.get();
        }

        String accessToken = createToken(email, 15 * 60 * 1000);
        String refreshToken = createToken(email, 7 * 24 * 60 * 60 * 1000);
        userEntity.setRefreshToken(refreshToken);
        userRepository.save(userEntity);

        String jsonResponse = getAuthResponse(userEntity, accessToken, refreshToken);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    private static String getAuthResponse(User userEntity, String accessToken, String refreshToken) throws JsonProcessingException {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("email", userEntity.getEmail());
        responseData.put("username", userEntity.getUsername());
        responseData.put("first_name", userEntity.getFirstName());
        responseData.put("last_name", userEntity.getLastName());
        responseData.put("access_token", accessToken);
        responseData.put("refresh_token", refreshToken);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(responseData);
    }

    public static String createToken(String email, long tokenLifespan) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenLifespan))
                .sign(Algorithm.HMAC256("secret")); // Use a strong, stored secret
    }
}
